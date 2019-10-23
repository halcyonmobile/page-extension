package com.halcyonmobile.pagination_extension

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.halcyonmobile.page.coroutine.db.dao.SuspendKeyDao
import com.halcyonmobile.pagination_extension.db.room.KeyEntity

@Dao
abstract class PageKeyDao : SuspendKeyDao<String, KeyEntity> {
    @Query("SELECT * FROM keyentity WHERE id = :id")
    abstract override suspend fun get(id: String): KeyEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun insert(keyEntity: KeyEntity)

    @Query("DELETE from keyentity WHERE id = :id")
    abstract suspend fun delete(id: String)
}