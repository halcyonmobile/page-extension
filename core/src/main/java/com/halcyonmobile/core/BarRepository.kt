package com.halcyonmobile.core

import com.halcyonmobile.page.coroutine.ChannelBasedDataSourceUpdateListener
import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.page.coroutine.SuspendKeyLocalStorage
import com.halcyonmobile.page.coroutine.SuspendKeyLocalStorageAdapter
import com.halcyonmobile.page.coroutine.SuspendProvideDataByPagedKeyAndSize
import com.halcyonmobile.page.coroutine.SuspendValueLocalCacherAdapter
import com.halcyonmobile.page.coroutine.SuspendValueLocalStorage
import com.halcyonmobile.page.db.StateProvidingBoundaryCallBack
import kotlinx.coroutines.CoroutineScope

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class BarRepository(
    private val barRemoteSource: BarRemoteSource,
    private val barLocalSource: SuspendValueLocalStorage<Int, Bar>,
    private val keyLocalStorage: SuspendKeyLocalStorage<Int, Bar>
) {

    fun get(coroutineScope: CoroutineScope, networkPageSize: Int): PagedResult<Int, Bar, NetworkError> {
        val channelBasedDataSourceUpdateListener = ChannelBasedDataSourceUpdateListener<NetworkError>()
        val dataSourceFactory = barLocalSource.getValueFactory()
        val stateProvidingBoundaryCallback = StateProvidingBoundaryCallBack(
            dataSourceUpdateListener = channelBasedDataSourceUpdateListener,
            networkPageSize = 20, // todo come from the datasource?
            valueLocalCacher = SuspendValueLocalCacherAdapter(coroutineScope, barLocalSource),
            keyLocalStorage = SuspendKeyLocalStorageAdapter(coroutineScope, keyLocalStorage),
            initialKey = 0,
            provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope, barRemoteSource::get)
        )
        return PagedResult(
            dataSourceFactory = dataSourceFactory,
            stateChannel = channelBasedDataSourceUpdateListener.stateChannel,
            boundaryCallback = stateProvidingBoundaryCallback
        )
    }
}