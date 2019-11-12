/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.halcyonmobile.pagination_extension.db.room.dao.BarDao
import com.halcyonmobile.pagination_extension.db.room.dao.ItemCountDao
import com.halcyonmobile.pagination_extension.db.room.dao.PageKeyDao
import com.halcyonmobile.pagination_extension.db.room.entity.BarEntity
import com.halcyonmobile.pagination_extension.db.room.entity.ItemCountEntity
import com.halcyonmobile.pagination_extension.db.room.entity.KeyEntity

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
@Database(entities = [BarEntity::class, KeyEntity::class, ItemCountEntity::class], version = 1)
abstract class BarDataBase : RoomDatabase(){
    abstract val barDao: BarDao
    abstract val keyDao: PageKeyDao
    abstract val itemCountDao: ItemCountDao
}