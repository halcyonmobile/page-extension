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
package com.halcyonmobile.core.simple

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.BarDBKeyLocalStorage
import com.halcyonmobile.core.shared.BarDBLocalSource
import com.halcyonmobile.core.shared.BarRemoteSource
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.page.coroutine.createPagedResultFromDao
import kotlinx.coroutines.CoroutineScope

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
    private val keyLocalStorage: BarDBKeyLocalStorage
) : SimpleRepository {

    override fun get(coroutineScope: CoroutineScope): PagedResult<Int, Bar, NetworkError> =
        createPagedResultFromDao(
            coroutineScope = coroutineScope,
            networkPageSize = 10,
            keyLocalStorage = keyLocalStorage,
            valueLocalStorage = barLocalSource,
            request = barRemoteSource::get,
            initialPageKey = 0
        )

    override suspend fun fetch(){
        keyLocalStorage.clear()
        barLocalSource.clear()
    }
}