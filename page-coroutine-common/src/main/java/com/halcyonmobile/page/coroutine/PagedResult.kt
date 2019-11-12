/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page.coroutine

import androidx.paging.DataSource
import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Contains everything that is needed to create a PagedList.
 *
 * This expected to be returned from repositories using pagination.
 */
data class PagedResult<Key, Value, Error>(
    val stateChannel: ReceiveChannel<DataSourceState<Error>>,
    override val dataSourceFactory: DataSource.Factory<Key, Value>,
    override val boundaryCallback: PagedList.BoundaryCallback<Value>? = null
) : com.halcyonmobile.page.PagedResult<Key, Value>