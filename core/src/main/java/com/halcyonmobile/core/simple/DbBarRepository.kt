/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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