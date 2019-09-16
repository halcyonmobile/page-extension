package com.halcyonmobile.core

import kotlinx.coroutines.delay

class BarRemoteSource {

    suspend fun get(key: Int, pageSize: Int): Pair<List<Bar>, Int> {
        delay(100)
        return (0 until pageSize).map {
            val index = key * pageSize + it
            Bar(index, "index: $index")
        } to (key + 1)
    }

}