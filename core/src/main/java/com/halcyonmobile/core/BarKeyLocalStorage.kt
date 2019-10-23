package com.halcyonmobile.core

import com.halcyonmobile.page.coroutine.db.localstorage.SuspendKeyLocalStorage

interface BarKeyLocalStorage : SuspendKeyLocalStorage<Int, Bar>{

    /**
     * Clears the page-key from the database for bar items.
     */
    suspend fun clear()
}