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
package com.halcyonmobile.pagination_extension.db.sources

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.BarDBLocalSource
import com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendLocalStorage
import com.halcyonmobile.pagination_extension.db.room.dao.BarDao
import com.halcyonmobile.pagination_extension.db.room.entity.BarEntity

class BarLocalSourceImpl(private val barDao: BarDao) : PagedSuspendLocalStorage<Int, Bar, BarEntity>(barDao), BarDBLocalSource {

    override fun valueEntityToValue(valueEntity: BarEntity): Bar = Bar(id = valueEntity.id, title = valueEntity.something)

    override fun valueToValueEntry(value: Bar): BarEntity =
        BarEntity(id = value.id, something = value.title)

    override suspend fun clear() {
        barDao.clear()
    }

}