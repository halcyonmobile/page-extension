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
    //@Query("SELECT * from barEntity ORDER BY id")
    fun getValueEntitiesFactory(): DataSource.Factory<Key, ValueEntity>

    suspend fun insert(valueEntities: List<ValueEntity>)

    fun valueToValueEntry(value: Value): ValueEntity

    fun valueEntityToValue(valueEntity: ValueEntity): Value
}