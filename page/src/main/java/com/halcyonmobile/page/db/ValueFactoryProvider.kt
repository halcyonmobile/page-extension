package com.halcyonmobile.page.db

import androidx.paging.DataSource

interface ValueFactoryProvider<Key, Value>{

    fun getValueFactory(): DataSource.Factory<Key, Value>
}