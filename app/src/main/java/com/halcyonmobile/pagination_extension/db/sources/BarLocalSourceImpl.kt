package com.halcyonmobile.pagination_extension.db.sources

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.BarLocalSource
import com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendLocalStorage
import com.halcyonmobile.pagination_extension.db.room.BarEntity
import com.halcyonmobile.pagination_extension.db.room.BarDao

class BarLocalSourceImpl(private val barDao: BarDao) : PagedSuspendLocalStorage<Int, Bar, BarEntity>(barDao), BarLocalSource {

    override fun valueEntityToValue(valueEntity: BarEntity): Bar = Bar(id = valueEntity.id, title = valueEntity.something)

    override fun valueToValueEntry(value: Bar): BarEntity =
        BarEntity(id = value.id, something = value.title)

    override suspend fun clear() {
        barDao.clear()
    }

}