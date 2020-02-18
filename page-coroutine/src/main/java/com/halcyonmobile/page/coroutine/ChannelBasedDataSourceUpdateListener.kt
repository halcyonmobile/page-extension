/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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