/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.core.shared

import com.halcyonmobile.core.Bar
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendValueLocalStorage

interface BarDBLocalSource : SuspendValueLocalStorage<Int, Bar> {

    /**
     * Clears the database.
     */
    suspend fun clear()
}