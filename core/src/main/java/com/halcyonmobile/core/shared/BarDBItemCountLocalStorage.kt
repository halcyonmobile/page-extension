/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.core.shared

import com.halcyonmobile.page.coroutine.db.localstorage.SuspendAdditionalDataLocalStorage

interface BarDBItemCountLocalStorage : SuspendAdditionalDataLocalStorage<Int> {

    /**
     * Clears the database.
     */
    suspend fun clear()
}