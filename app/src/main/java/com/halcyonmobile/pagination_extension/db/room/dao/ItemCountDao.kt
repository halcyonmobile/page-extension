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