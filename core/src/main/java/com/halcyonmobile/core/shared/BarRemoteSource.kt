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
package com.halcyonmobile.core.shared

import com.halcyonmobile.core.Bar
import kotlinx.coroutines.delay

class BarRemoteSource {

    private val numberOfElements = 100

    suspend fun get(key: Int, pageSize: Int): Pair<List<Bar>, Int> {
        val response = getWithItemCount(key, pageSize)
        return response.first to response.second
    }

    suspend fun getWithItemCount(key: Int, pageSize: Int): Triple<List<Bar>, Int, Int> {
        delay(2000)
        val page = if (key * pageSize > numberOfElements) emptyList() else (0 until pageSize).map {
            val index = key * pageSize + it
            Bar(index, "index: $index")
        }.takeWhile { it.id <= numberOfElements }

        return Triple(page,key + 1, numberOfElements)
    }
}