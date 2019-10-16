package com.halcyonmobile.page.coroutine

import androidx.paging.DataSource

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
interface SuspendPagedDao<Key, Value, ValueEntity> {

    fun getValueEntitiesFactory(): DataSource.Factory<Key, ValueEntity>

    suspend fun insert(valueEntities: List<@JvmSuppressWildcards ValueEntity>)
}