/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page.coroutine.db.localstorage

import com.halcyonmobile.page.db.KeyOrEndOfList

/**
 * Interface defining how the page-keys should be stored in localStorage
 */
interface SuspendKeyLocalStorage<Key, Value> {

    /**
     * Saves the given [key] into a database.
     */
    suspend fun cache(key: KeyOrEndOfList<Key>)

    /**
     * Returns the [key][KeyOrEndOfList] from the database.
     */
    suspend fun getKey(value: Value): KeyOrEndOfList<Key>
}