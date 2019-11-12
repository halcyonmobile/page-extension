/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page.db

import androidx.paging.DataSource

/**
 * Simple interface that the LocalStorage should implement to provide the [DataSource]
 */
interface ValueFactoryProvider<Key, Value>{

    /**
     * Returns the [DataSource.Factory] from the database.
     */
    fun getValueFactory(): DataSource.Factory<Key, Value>
}