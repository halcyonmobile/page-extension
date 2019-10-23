package com.halcyonmobile.core

import com.halcyonmobile.page.coroutine.db.dao.SuspendPagedDao
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendValueLocalStorage

interface BarLocalSource : SuspendValueLocalStorage<Int, Bar>{

    /**
     * Clears the database.
     */
    suspend fun clear()
}