package com.halcyonmobile.page.db

import androidx.paging.DataSource

/**
 * Simple interface that the LocalStorage should implement to provide the [DataSource]
 */
interface ValueFactoryProvider<Key, Value>{

    fun getValueFactory(): DataSource.Factory<Key, Value>
}