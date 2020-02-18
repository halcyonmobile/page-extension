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
package com.halcyonmobile.core.withitemcount

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.BarDBItemCountLocalStorage
import com.halcyonmobile.core.shared.BarDBKeyLocalStorage
import com.halcyonmobile.core.shared.BarDBLocalSource
import com.halcyonmobile.core.shared.BarRemoteSource
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.core.withitemcount.WithItemCountRepository.Companion.INVALID_ITEM_COUNT
import com.halcyonmobile.page.coroutine.PagedResultWithAdditionalData
import com.halcyonmobile.page.coroutine.createPagedResultWithAdditionalDataFromDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class DbBarRepository(
    private val barRemoteSource: BarRemoteSource,
    private val barLocalSource: BarDBLocalSource,
    private val keyLocalStorage: BarDBKeyLocalStorage,
    private val barItemCountLocalStorage: BarDBItemCountLocalStorage
) : WithItemCountRepository {

    private val itemCountChannel = ConflatedBroadcastChannel<Int>()

    override fun get(coroutineScope: CoroutineScope): PagedResultWithAdditionalData<Int, Bar, Int, NetworkError> =
        createPagedResultWithAdditionalDataFromDao(
            coroutineScope = coroutineScope,
            networkPageSize = 10,
            keyLocalStorage = keyLocalStorage,
            valueLocalStorage = barLocalSource,
            additionalDataLocalStorage = barItemCountLocalStorage,
            request = barRemoteSource::getWithItemCount,
            additionalDataChannel = itemCountChannel,
            initialPageKey = 0
        )

    override suspend fun fetch() {
        barItemCountLocalStorage.clear()
        itemCountChannel.offer(INVALID_ITEM_COUNT)
        keyLocalStorage.clear()
        barLocalSource.clear()
    }
}