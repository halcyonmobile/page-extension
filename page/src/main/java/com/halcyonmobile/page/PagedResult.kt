/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.page

import androidx.paging.DataSource
import androidx.paging.PagedList

/**
 * Contains everything that is needed to create a PagedList.
 *
 * This expected to be returned from repositories using pagination.
 */
interface PagedResult<Key, Value> {
    val dataSourceFactory: DataSource.Factory<Key, Value>
    val boundaryCallback: PagedList.BoundaryCallback<Value>?
}