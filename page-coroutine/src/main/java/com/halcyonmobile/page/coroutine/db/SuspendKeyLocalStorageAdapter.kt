package com.halcyonmobile.page.coroutine.db

import com.halcyonmobile.page.coroutine.db.localstorage.SuspendKeyLocalStorage
import com.halcyonmobile.page.db.KeyLocalStorage
import com.halcyonmobile.page.db.KeyOrEndOfList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Adapter class to adapt a [SuspendKeyLocalStorage] into a [KeyLocalStorage]
 */
class SuspendKeyLocalStorageAdapter<Key, Value>(
    private val coroutineScope: CoroutineScope,
    private val suspendKeyLocalStorage: SuspendKeyLocalStorage<Key, Value>
) : KeyLocalStorage<Key, Value> {

    override fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit) {
        coroutineScope.launch {
            suspendKeyLocalStorage.cache(key)
            callback()
        }
    }

    override fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit) {
        coroutineScope.launch {
            callback(suspendKeyLocalStorage.getKey(value))
        }
    }
}