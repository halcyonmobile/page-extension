/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.core.shared

import com.halcyonmobile.core.Bar
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendKeyLocalStorage

interface BarDBKeyLocalStorage : SuspendKeyLocalStorage<Int, Bar> {

    /**
     * Clears the page-key from the database for bar items.
     */
    suspend fun clear()
}