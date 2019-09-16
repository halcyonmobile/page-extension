package com.halcyonmobile.core

import kotlinx.coroutines.delay

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class FooRemoteSource {

    suspend fun get(key: Int, pageSize: Int): Pair<List<Int>, Int> {
        delay(100)
        return (0 until pageSize).map {
            key * pageSize + it
        } to (key + 1)
    }

}