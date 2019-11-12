/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.db.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.halcyonmobile.page.coroutine.db.dao.SuspendAdditionalDataDao
import com.halcyonmobile.pagination_extension.db.room.entity.ItemCountEntity

@Dao
abstract class ItemCountDao : SuspendAdditionalDataDao<String, ItemCountEntity> {

    @Query("SELECT * FROM itemcountentity WHERE id = :id")
    abstract override suspend fun get(id: String): ItemCountEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun insert(additionalData: ItemCountEntity)

    @Query("DELETE FROM itemcountentity WHERE id = :id")
    abstract suspend fun delete(id: String)

}