/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.core.simple

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.page.coroutine.PagedResult
import kotlinx.coroutines.CoroutineScope

interface SimpleRepository{

    fun get(coroutineScope: CoroutineScope): PagedResult<Int, Bar, NetworkError>

    suspend fun fetch()
}