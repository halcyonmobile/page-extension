/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.withitemcount

import android.content.Context
import androidx.room.Room
import com.halcyonmobile.core.shared.BarRemoteSource
import com.halcyonmobile.core.withitemcount.DbBarRepository
import com.halcyonmobile.core.withitemcount.InMemoryBarRepository
import com.halcyonmobile.core.withitemcount.WithItemCountRepository
import com.halcyonmobile.pagination_extension.db.room.BarDataBase
import com.halcyonmobile.pagination_extension.db.sources.BarItemCountLocalSource
import com.halcyonmobile.pagination_extension.db.sources.BarLocalSourceImpl
import com.halcyonmobile.pagination_extension.db.sources.BarPageKeyLocalSource

class WithItemCountRepositoryFactory(private val context: Context) {

    fun get(type: Type) : WithItemCountRepository =
        when(type){
            Type.IN_MEMORY -> InMemoryBarRepository(BarRemoteSource())

            Type.DB -> {
                val db = Room.databaseBuilder(context, BarDataBase::class.java, "BarDataBase").build()
                DbBarRepository(
                    BarRemoteSource(),
                    BarLocalSourceImpl(db.barDao),
                    BarPageKeyLocalSource(db.keyDao),
                    BarItemCountLocalSource(db.itemCountDao)
                )
            }
        }

    enum class Type{
        IN_MEMORY, DB
    }
}