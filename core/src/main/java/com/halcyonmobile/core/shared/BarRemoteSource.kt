/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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