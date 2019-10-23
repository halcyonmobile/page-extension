package com.halcyonmobile.pagination_extension.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.halcyonmobile.pagination_extension.PageKeyDao

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
@Database(entities = [BarEntity::class, KeyEntity::class], version = 1)
abstract class BarDataBase : RoomDatabase(){
    abstract val barDao: BarDao
    abstract val keyDao: PageKeyDao
}