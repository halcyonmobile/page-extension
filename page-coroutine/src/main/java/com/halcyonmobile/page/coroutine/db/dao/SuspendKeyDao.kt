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