package com.halcyonmobile.page.coroutine.db.dao

import androidx.paging.DataSource

/**
 * A template interface to show what dao functions has to be provided for the [com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendLocalStorage]
 */
interface SuspendPagedDao<Key, Value, ValueEntity> {

    /**
     * Returns a [DataSource.Factory] for the values.
     *
     * Should be annotated with something similar to
     * ```
     * @Query("SELECT * from barEntity ORDER BY name")
     * ```
     * where barEntity is the table and name is what the paged-api is sorted by
     */
    fun getValueEntitiesFactory(): DataSource.Factory<Key, ValueEntity>

    /**
     * Inserts the [valueEntities] into the database.
     *
     * Should be annotated with something similar to
     * ```
     * @Insert(onConflict = OnConflictStrategy.REPLACE)
     * ```
     */
    suspend fun insert(valueEntities: List<@JvmSuppressWildcards ValueEntity>)
}