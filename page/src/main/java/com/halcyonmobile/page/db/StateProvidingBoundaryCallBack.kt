package com.halcyonmobile.page.db

import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import com.halcyonmobile.page.DataSourceUpdateListener
import com.halcyonmobile.page.ProvideDataByPageKeyAndSize
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A [PagedList.BoundaryCallback] which provides state in via [dataSourceUpdateListener]
 * [provideDataByPageKeyAndSize] a request which gets data from the network from a key
 * [keyLocalStorage] local storage of the page key, save and get is used.
 * [valueLocalCacher] a local storage which is able to cache the returned values, it should save into the database.
 * [initialKey] the initial key used if no items are in the database
 * [networkPageSize] how much elements should be requested from the network
 */
class StateProvidingBoundaryCallBack<Key, Value, Error>(
    private val dataSourceUpdateListener: DataSourceUpdateListener<Error>,
    private val provideDataByPageKeyAndSize: ProvideDataByPageKeyAndSize<Value, Key, Error>,
    private val keyLocalStorage: KeyLocalStorage<Key, Value>,
    private val valueLocalCacher: ValueLocalCacher<Key, Value>,
    private val initialKey: Key,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<Value>() {

    private val isLoading = AtomicBoolean(false)

    override fun onZeroItemsLoaded() {
        if (!isLoading.compareAndSet(false, true)) {
            return
        }
        dataSourceUpdateListener(DataSourceState.InitialLoading())
        callRequest(initialKey, true, ::onZeroItemsLoaded)
        isLoading.set(false)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Value) {
        if (!isLoading.compareAndSet(false, true)) {
            return
        }
        dataSourceUpdateListener(DataSourceState.LoadingMore())
        keyLocalStorage.getKey(itemAtEnd) {
            when (it) {
                is KeyOrEndOfList.EndReached -> Unit
                is KeyOrEndOfList.Key -> callRequest(it.key, false) {
                    onItemAtEndLoaded(itemAtEnd)
                }
            }
        }
        isLoading.set(false)
    }

    private inline fun callRequest(key: Key, isInitial: Boolean, crossinline onRetry: () -> Unit) {
        provideDataByPageKeyAndSize(key, networkPageSize) {
            when (it) {
                is ProvideDataByPageKeyAndSize.Result.Success -> {
                    val nextKey = if (it.data.isEmpty()) KeyOrEndOfList.EndReached<Key>() else KeyOrEndOfList.Key(it.nextKey)
                    keyLocalStorage.cache(nextKey) {
                        valueLocalCacher.cache(it.data) {
                            val stateToPublish : DataSourceState<Error> = when{
                                it.data.isNotEmpty() -> DataSourceState.Normal()
                                isInitial -> DataSourceState.Empty()
                                else -> DataSourceState.EndReached()
                            }
                            dataSourceUpdateListener(stateToPublish)
                        }
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