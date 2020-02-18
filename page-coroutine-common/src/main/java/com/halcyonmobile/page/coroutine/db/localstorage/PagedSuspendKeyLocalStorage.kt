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

import com.halcyonmobile.page.coroutine.db.dao.SuspendKeyDao
import com.halcyonmobile.page.db.KeyOrEndOfList

/**
 * Basic implementation of the LocalSource to store the paged requests' page-keys.
 *
 * It's assumed that every keys for paged requests are contained in the same table, with different key,
 * so each request would have a key, which identifies that paged-request's page key.
 */
abstract class PagedSuspendKeyLocalStorage<Key, Value, KeyEntityId, KeyEntity>(
    protected val keyEntityId: KeyEntityId,
    protected val keyDao: SuspendKeyDao<KeyEntityId, KeyEntity>
) : SuspendKeyLocalStorage<Key, Value> {

    override suspend fun cache(key: KeyOrEndOfList<Key>) {
        val entity = if (key is KeyOrEndOfList.Key<Key>){
            keyToKeyEntity(key.key)
        } else {
            endToKeyEntity()
        }
        keyDao.insert(entity)
    }

    override suspend fun getKey(value: Value): KeyOrEndOfList<Key> =
        keyEntityToKey(keyDao.get(keyEntityId))

    /**
     * Converts [KeyOrEndOfList.EndReached] to a [KeyEntity] that can be stored in the [keyDao]
     */
    abstract fun endToKeyEntity() : KeyEntity

    /**
     * Converts the key hold in [KeyOrEndOfList.Key] into a [KeyEntity] that can be stored in the [keyDao]
     */
    abstract fun keyToKeyEntity(key: Key): KeyEntity

    /**
     * Converts the [keyEntity] into a [KeyOrEndOfList] which can be returned for the repository.
     */
    abstract fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Key>
}