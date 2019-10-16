package com.halcyonmobile.page.db

import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import com.halcyonmobile.page.DataSourceUpdateListener
import com.halcyonmobile.page.ProvideDataByPageKeyAndSize

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class StateProvidingBoundaryCallBack<Key, Value, Error>(
    private val dataSourceUpdateListener: DataSourceUpdateListener<Error>,
    private val provideDataByPageKeyAndSize: ProvideDataByPageKeyAndSize<Value, Key, Error>,
    private val keyLocalStorage: KeyLocalStorage<Key, Value>,
    private val valueLocalCacher: ValueLocalCacher<Key, Value>,
    private val initialKey: Key,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<Value>() {
    override fun onZeroItemsLoaded() {
        dataSourceUpdateListener(DataSourceState.InitialLoading())
        callRequest(initialKey, true, ::onZeroItemsLoaded)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Value) {
        dataSourceUpdateListener(DataSourceState.LoadingMore())
        keyLocalStorage.getKey(itemAtEnd) {
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
                    val nextKey = if (it.data.isEmpty()) KeyOrEndOfList.EndReached<Key>() else KeyOrEndOfList.Key(it.nextKey)
                    keyLocalStorage.cache(nextKey) {
                        valueLocalCacher.cache(it.data) {
                            dataSourceUpdateListener(if (handleEmptyState && it.data.isEmpty()) DataSourceState.Empty() else DataSourceState.Normal())
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