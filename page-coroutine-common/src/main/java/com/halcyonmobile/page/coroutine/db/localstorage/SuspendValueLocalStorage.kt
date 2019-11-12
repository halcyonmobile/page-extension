/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page.coroutine.db.localstorage

import com.halcyonmobile.page.db.ValueFactoryProvider

/**
 * Interface defining how to access the [androidx.paging.DataSource.Factory] and how the cache values into the database.
 */
interface SuspendValueLocalStorage<Key, Value> : ValueFactoryProvider<Key, Value> {

    /**
     * Caches the given [values] into the database.
     */
    suspend fun cache(values: List<Value>)
}