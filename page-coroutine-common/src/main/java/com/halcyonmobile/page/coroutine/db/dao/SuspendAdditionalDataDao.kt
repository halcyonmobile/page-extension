/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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