/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.simple

import android.content.Context
import androidx.room.Room
import com.halcyonmobile.core.shared.BarRemoteSource
import com.halcyonmobile.core.simple.DbBarRepository
import com.halcyonmobile.core.simple.InMemoryBarRepository
import com.halcyonmobile.core.simple.SimpleRepository
import com.halcyonmobile.pagination_extension.db.room.BarDataBase
import com.halcyonmobile.pagination_extension.db.sources.BarLocalSourceImpl
import com.halcyonmobile.pagination_extension.db.sources.BarPageKeyLocalSource

class SimpleRepositoryFactory(private val context: Context) {

    fun get(type: Type) : SimpleRepository =
        when(type){
            Type.IN_MEMORY -> InMemoryBarRepository(BarRemoteSource())

            Type.DB -> {
                val db = Room.databaseBuilder(context, BarDataBase::class.java, "BarDataBase").build()
                DbBarRepository(
                    BarRemoteSource(),
                    BarLocalSourceImpl(db.barDao),
                    BarPageKeyLocalSource(db.keyDao)
                )
            }
        }

    enum class Type{
        IN_MEMORY, DB
    }
}