package com.halcyonmobile.page.db

import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import com.halcyonmobile.page.DataSourceUpdateListener
import com.halcyonmobile.page.ProvideDataByPageKeyAndSize

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