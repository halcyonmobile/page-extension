/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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