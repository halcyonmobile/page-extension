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
import com.halcyonmobile.core.shared.BarRemoteSource
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.page.DataSourceInvalidator
import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.page.coroutine.createPagedResultFromRequest
import kotlinx.coroutines.CoroutineScope

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class InMemoryBarRepository(private val barRemoteSource: BarRemoteSource) : SimpleRepository {

    private val invalidator = DataSourceInvalidator<Int, Bar>()

    override fun get(coroutineScope: CoroutineScope): PagedResult<Int, Bar, NetworkError> =
        createPagedResultFromRequest(
            coroutineScope = coroutineScope,
            dataSourceInvalidator = invalidator,
            request = { key: Int, pageSize : Int -> barRemoteSource.get(key, pageSize) },
            initialPageKey = 0
        )

    override suspend fun fetch() {
        invalidator()
    }
}