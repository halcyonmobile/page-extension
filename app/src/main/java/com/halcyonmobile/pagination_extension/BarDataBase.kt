package com.halcyonmobile.pagination_extension

import androidx.room.Database
import androidx.room.RoomDatabase

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