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
package com.halcyonmobile.page.db

/**
 * An abstract implementation of [KeyLocalStorage] which assumes the page [Key] can be extracted from the last data value.
 */
abstract class ValueToKeyMapper<Key, Value> : KeyLocalStorage<Key, Value> {
    private var didReachEnd = false

    final override fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit) {
        didReachEnd = key is KeyOrEndOfList.EndReached
        callback()
    }

    final override fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit) {
        if (didReachEnd) {
            callback(KeyOrEndOfList.EndReached())
        } else {
            callback(KeyOrEndOfList.Key(mapValueToKey(value)))
        }
    }

    /**
     * Extracts the [Key] from the [value]
     */
    abstract fun mapValueToKey(value: Value): Key
}

inline fun <Key, Value> ValueToKeyMapper(crossinline valueToKey: (Value) -> Key): ValueToKeyMapper<Key, Value> =
    object : ValueToKeyMapper<Key, Value>() {
        override fun mapValueToKey(value: Value): Key = valueToKey(value)
    }