/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.core.withitemcount

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.BarRemoteSource
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.page.DataSourceInvalidator
import com.halcyonmobile.page.coroutine.PagedResultWithAdditionalData
import com.halcyonmobile.page.coroutine.createPagedResultWithAdditionalDataFromRequest
import kotlinx.coroutines.CoroutineScope

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class InMemoryBarRepository(private val barRemoteSource: BarRemoteSource) : WithItemCountRepository {

    private val invalidator = DataSourceInvalidator<Int, Bar>()

    override fun get(coroutineScope: CoroutineScope): PagedResultWithAdditionalData<Int, Bar, Int, NetworkError> =
        createPagedResultWithAdditionalDataFromRequest(
            coroutineScope = coroutineScope,
            dataSourceInvalidator = invalidator,
            request = { key: Int, pageSize: Int -> barRemoteSource.getWithItemCount(key, pageSize) },
            initialPageKey = 0
        )

    override suspend fun fetch() {
        invalidator()
    }
}