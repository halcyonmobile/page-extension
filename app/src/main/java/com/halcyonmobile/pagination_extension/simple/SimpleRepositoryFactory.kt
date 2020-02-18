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