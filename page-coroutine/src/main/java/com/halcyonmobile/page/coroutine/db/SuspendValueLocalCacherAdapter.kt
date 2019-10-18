package com.halcyonmobile.page.coroutine.db

import com.halcyonmobile.page.coroutine.db.localstorage.SuspendValueLocalStorage
import com.halcyonmobile.page.db.ValueLocalCacher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Adapts a [SuspendValueLocalStorage] into a [ValueLocalCacher]
 */
class SuspendValueLocalCacherAdapter<Key, Value>(
    private val coroutineScope: CoroutineScope,
    private val suspendValueLocalStorage: SuspendValueLocalStorage<Key, Value>
) : ValueLocalCacher<Key, Value> {

    override fun cache(values: List<Value>, callback: () -> Unit) {
        coroutineScope.launch {
            suspendValueLocalStorage.cache(values)
            callback()
        }
    }
}