//package com.halcyonmobile.page.coroutine
//
//import com.halcyonmobile.page.db.KeyToValueMapperStorage
//import com.halcyonmobile.page.db.StateProvidingBoundaryCallBack
//import kotlinx.coroutines.CoroutineScope
//
///**
// * Purpose
// * <p>
// * Description
// * <p/>
// * Notes:
// * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
// */
//
//inline fun <Key, Value, KeyEntity, ValueEntity, reified Error : Throwable> createPagedResultFromDao(
//    coroutineScope: CoroutineScope,
//    initialPageKey: Key,
//    networkPageSize: Int,
//    pagedDao: SuspendPagedDao<Key, Value, ValueEntity>,
//    keyDao: SuspendKeyDao<Key, KeyEntity>,
//    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
//    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>
//): PagedResult<Key, Value, Error> {
//    val pagedLocalStorage = PagedLocalStorage(coroutineScope, pagedDao)
//    val boundaryCallback = StateProvidingBoundaryCallBack(
//        initialKey = initialPageKey,
//        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, pageSize ->
//            request(key, pageSize)
//        },
//        dataSourceUpdateListener = channelBasedDataSourceUpdateListener,
//        networkPageSize = networkPageSize, // todo can we somehow drop this? :thinking:
//        valueLocalCacher = pagedLocalStorage,
//        keyLocalStorage = PagedKeyLocalStorage(coroutineScope, keyDao)
//    )
//    return PagedResult(
//        boundaryCallback = boundaryCallback,
//        dataSourceFactory = pagedLocalStorage.getValueFactory(),
//        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
//    )
//}
//
//inline fun <Key, Value, ValueEntity, reified Error : Throwable> createPagedResultFromDao(
//    coroutineScope: CoroutineScope,
//    initialPageKey: Key,
//    networkPageSize: Int,
//    pagedDao: SuspendPagedDao<Key, Value, ValueEntity>,
//    channelBasedDataSourceUpdateListener: ChannelBasedDataSourceUpdateListener<Error> = ChannelBasedDataSourceUpdateListener(),
//    crossinline valueToKey: (Value) -> Key,
//    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>
//): PagedResult<Key, Value, Error> {
//    val pagedLocalStorage = PagedLocalStorage(coroutineScope, pagedDao)
//    val boundaryCallback = StateProvidingBoundaryCallBack(
//        initialKey = initialPageKey,
//        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope) { key, pageSize ->
//            request(key, pageSize)
//        },
//        dataSourceUpdateListener = channelBasedDataSourceUpdateListener,
//        networkPageSize = networkPageSize, // todo can we somehow drop this? :thinking:
//        valueLocalCacher = pagedLocalStorage,
//        keyLocalStorage = KeyToValueMapperStorage(valueToKey)
//    )
//    return PagedResult(
//        boundaryCallback = boundaryCallback,
//        dataSourceFactory = pagedLocalStorage.getValueFactory(),
//        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
//    )
//}