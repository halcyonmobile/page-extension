/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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