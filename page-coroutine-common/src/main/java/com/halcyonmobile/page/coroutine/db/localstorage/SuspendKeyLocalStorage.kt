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