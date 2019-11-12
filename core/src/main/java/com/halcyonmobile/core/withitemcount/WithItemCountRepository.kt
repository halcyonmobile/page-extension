/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.core.withitemcount

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.page.coroutine.PagedResultWithAdditionalData
import kotlinx.coroutines.CoroutineScope

interface WithItemCountRepository{

    fun get(coroutineScope: CoroutineScope): PagedResultWithAdditionalData<Int, Bar, Int, NetworkError>

    suspend fun fetch()

    companion object{
        const val INVALID_ITEM_COUNT = -1
    }
}