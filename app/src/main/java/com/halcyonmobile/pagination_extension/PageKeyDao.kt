package com.halcyonmobile.pagination_extension

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.halcyonmobile.page.coroutine.db.dao.SuspendKeyDao

@Dao
abstract class PageKeyDao : SuspendKeyDao<String, KeyEntity> {
    @Query("SELECT * FROM keyentity WHERE id = :id")
    abstract override suspend fun get(id: String): KeyEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun insert(keyEntity: KeyEntity)
}