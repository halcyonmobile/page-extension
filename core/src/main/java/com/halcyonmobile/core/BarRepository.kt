package com.halcyonmobile.core

import androidx.paging.DataSource
import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import com.halcyonmobile.page.DataSourceUpdateListener
import com.halcyonmobile.page.ProvideDataByPageKeyAndSize
import com.halcyonmobile.page.coroutine.ChannelBasedDataSourceUpdateListener
import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.page.coroutine.SuspendPagedDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class BarRepository(private val barLocalSource: BarLocalSource, private val barDao: SuspendPagedDao<Int, Bar, BarEntity>) {

    fun get(coroutineScope: CoroutineScope, networkPageSize: Int): PagedResult<Int, Bar, NetworkError> {
        val channelBasedDataSourceUpdateListener = ChannelBasedDataSourceUpdateListener<NetworkError>()
        val dataSourceFactory = barLocalSource.get()
        return PagedResult(
            dataSourceFactory = dataSourceFactory,
            stateChannel = channelBasedDataSourceUpdateListener.stateChannel,
            boundaryCallback = ABoundaryCallBack(coroutineScope, channelBasedDataSourceUpdateListener, barRemoteSource, barLocalSource)
        )
    }
}
//interface BarLocalSource{
//    fun get() : DataSource.Factory<Int, Bar>
//
//    suspend fun add(bar: Bar)
//
//    suspend fun addAll(bar: List<Bar>)
//
//    suspend fun delete(bar: Bar)
//}

abstract class PagedValueDao<Key, Value, ValueEntity>{

    protected abstract fun getDataSourceEntityFactory() : DataSource.Factory<Key, ValueEntity>

    fun getDataSourceFactory() : DataSource.Factory<Key, Value> =
        getDataSourceEntityFactory().map(::mapValueEntityToValue)

    protected abstract fun insertEntities(valueEntities:List<ValueEntity>)

    fun insert(values: List<Value>){
        insertEntities(values.map(::mapValueToValueEntity))
    }

    protected abstract fun mapValueToValueEntity(value: Value) : ValueEntity

    protected abstract fun mapValueEntityToValue(valueEntity: ValueEntity) : Value

}

abstract class PagedKeyDao<Key, KeyEntity>{

    protected abstract fun insertKeyEntity(key: KeyOrEndOfList<KeyEntity>, callback: () -> Unit)

    fun insertKey(key: KeyOrEndOfList<Key>, callback: () -> Unit){
        val keyEntity : KeyOrEndOfList<KeyEntity> = when(key){
            is KeyOrEndOfList.EndReached -> KeyOrEndOfList.EndReached()
            is KeyOrEndOfList.Key -> KeyOrEndOfList.Key(mapKeyToKeyEntity(key.key))
        }
        insertKeyEntity(keyEntity, callback)
    }

    protected abstract fun getKeyEntity(callback: (KeyOrEndOfList<KeyEntity>) -> Unit)

    fun getKey(callback: (KeyOrEndOfList<Key>) -> Unit){
        getKeyEntity {
            callback(when(it){
                is KeyOrEndOfList.EndReached -> KeyOrEndOfList.EndReached()
                is KeyOrEndOfList.Key -> KeyOrEndOfList.Key(mapKeyEntityToKey(it.key))
            })
        }
    }

    protected abstract fun mapKeyToKeyEntity(value: Key) : KeyEntity

    protected abstract fun mapKeyEntityToKey(valueEntity: KeyEntity) : Key
}

sealed class KeyOrEndOfList<Key> {
    class EndReached<Key> : KeyOrEndOfList<Key>()
    data class Key<Key>(val key: Key) : KeyOrEndOfList<Key>()
}

interface DatabaseCache<Key, Value> {
    fun cache(key: Key, values: List<Value>, callback: () -> Unit)

    fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit)
}

class StateProvidingBoundaryCallBack<Key, Value, Error>(
    private val dataSourceUpdateListener: DataSourceUpdateListener<Error>,
    private val provideDataByPageKeyAndSize: ProvideDataByPageKeyAndSize<Value, Key, Error>,
    private val databaseCache: DatabaseCache<Key, Value>,
    private val initialKey: Key,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<Value>() {
    override fun onZeroItemsLoaded() {
        dataSourceUpdateListener(DataSourceState.InitialLoading())
        callRequest(initialKey, true, ::onZeroItemsLoaded)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Value) {
        dataSourceUpdateListener(DataSourceState.LoadingMore())
        databaseCache.getKey(itemAtEnd) {
            when (it) {
                is KeyOrEndOfList.EndReached -> Unit
                is KeyOrEndOfList.Key -> callRequest(it.key, false){
                    onItemAtEndLoaded(itemAtEnd)
                }
            }
        }
    }

    private inline fun callRequest(key: Key, handleEmptyState: Boolean, crossinline onRetry :() -> Unit) {
        provideDataByPageKeyAndSize(key, networkPageSize) {
            when (it) {
                is ProvideDataByPageKeyAndSize.Result.Success -> {
                    databaseCache.cache(it.nextKey, it.data) {
                        dataSourceUpdateListener(if (handleEmptyState && it.data.isEmpty()) DataSourceState.Empty() else DataSourceState.Normal())
                    }
                }
                is ProvideDataByPageKeyAndSize.Result.Error -> {
                    dataSourceUpdateListener(DataSourceState.ErrorLoadingInitial(it.error) {
                        onRetry()
                    })
                }
            }
        }
    }
}

class ABoundaryCallBack(
    val coroutineScope: CoroutineScope,
    val channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<NetworkError>,
    val barRemoteSource: BarRemoteSource,
    val barLocalSource: BarLocalSource
) : PagedList.BoundaryCallback<Bar>() {
    override fun onZeroItemsLoaded() {
        channelBasedDataSourceUpdateListener(DataSourceState.InitialLoading())
        coroutineScope.launch {
            try {
                // todo how to get the pagesize?
                // todo what to do with the key? :hmmmm: save into the database?
                val a = barRemoteSource.get(0, 40)
                if (a.first.isEmpty()) {
                    channelBasedDataSourceUpdateListener.invoke(DataSourceState.Empty())
                } else {
                    barLocalSource.addAll(a.first)
                    channelBasedDataSourceUpdateListener.invoke(DataSourceState.Normal())
                }
            } catch (networkError: NetworkError) {
                channelBasedDataSourceUpdateListener.invoke(DataSourceState.ErrorLoadingInitial(networkError) {
                    onZeroItemsLoaded()
                })
            }
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Bar) {
        channelBasedDataSourceUpdateListener(DataSourceState.LoadingMore())
        coroutineScope.launch {
            try {
                // todo fetch?
                // todo reached the end?
                delay(4000)
                val a = barRemoteSource.get(itemAtEnd.id / 20 + 1, 20)
                barLocalSource.addAll(a.first)
                channelBasedDataSourceUpdateListener.invoke(DataSourceState.Normal())
            } catch (networkError: NetworkError) {
                channelBasedDataSourceUpdateListener.invoke(DataSourceState.ErrorLoadingMore(networkError) {
                    onItemAtEndLoaded(itemAtEnd)
                })
            }
        }
    }
}