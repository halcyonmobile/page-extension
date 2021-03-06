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
 * A template interface to show what dao functions has to be provided for the [com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendAdditionalDataLocalStorage]
 */
interface SuspendAdditionalDataDao<AdditionalDataKey, AdditionalDataEntity> {
    /**
     * Returns the [AdditionalDataEntity] from the [id].
     *
     * Should be annotated with something similar to
     * ```
     * @Query("SELECT * FROM additionaldata WHERE id = :id")
     * ```
     * where additionaldata is the table.
     */
    suspend fun get(id: AdditionalDataKey): AdditionalDataEntity

    /**
     * Replaces the [AdditionalDataEntity].
     *
     * Should be annotated with something similar to
     * ```
     * @Insert(onConflict = OnConflictStrategy.REPLACE)
     * ```
     */
    suspend fun insert(additionalData: AdditionalDataEntity)
}