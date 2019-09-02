/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.page.coroutine

import com.halcyonmobile.page.DataSourceState
import com.halcyonmobile.page.DataSourceUpdateListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * A [DataSourceUpdateListener] which provides a [stateChannel] to listen to [DataSourceState] updates.
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
class ChannelBasedDataSourceUpdateListener<Error> : DataSourceUpdateListener<Error> {

    private val _stateChannel = ConflatedBroadcastChannel<DataSourceState<Error>>()
    val stateChannel get() = _stateChannel.openSubscription()

    override fun invoke(state: DataSourceState<Error>) {
        _stateChannel.offer(state)
    }

}