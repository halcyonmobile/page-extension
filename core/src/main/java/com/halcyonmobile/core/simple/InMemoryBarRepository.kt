/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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