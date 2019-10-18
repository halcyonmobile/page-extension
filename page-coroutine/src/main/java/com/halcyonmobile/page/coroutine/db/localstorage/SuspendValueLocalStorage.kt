package com.halcyonmobile.page.coroutine.db.localstorage

import com.halcyonmobile.page.db.ValueFactoryProvider

interface SuspendValueLocalStorage<Key, Value> : ValueFactoryProvider<Key, Value> {

    suspend fun cache(values: List<Value>)
}