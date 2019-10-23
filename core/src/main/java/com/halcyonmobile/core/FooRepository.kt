package com.halcyonmobile.core

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
class FooRepository(private val fooRemoteSource: FooRemoteSource) {

    private val invalidator = DataSourceInvalidator<Int, Int>()

    fun get(coroutineScope: CoroutineScope): PagedResult<Int, Int, NetworkError> {
        return createPagedResultFromRequest(
            coroutineScope = coroutineScope,
            dataSourceInvalidator = invalidator,
            request = { key: Int, pageSize : Int -> fooRemoteSource.get(key, pageSize) },
            initialPageKey = 0
        )
    }

    fun fetch() {
        invalidator()
    }
}