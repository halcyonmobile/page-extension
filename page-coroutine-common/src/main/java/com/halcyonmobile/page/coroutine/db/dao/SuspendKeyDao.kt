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
package com.halcyonmobile.page.coroutine.db.dao

/**
 * A template interface to show what dao functions has to be provided for the [com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendKeyLocalStorage]
 */
interface SuspendKeyDao<KeyEntityId, KeyEntity> {
    /**
     * Returns the [KeyEntity] from the [id].
     *
     * Should be annotated with something similar to
     * ```
     * @Query("SELECT * FROM keyentity WHERE id = :id")
     * ```
     * where keyentity is the table.
     */
    suspend fun get(id: KeyEntityId): KeyEntity

    /**
     * Replaces the [KeyEntity].
     *
     * Should be annotated with something similar to
     * ```
     * @Insert(onConflict = OnConflictStrategy.REPLACE)
     * ```
     */
    suspend fun insert(keyEntity: KeyEntity)
}