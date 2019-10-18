package com.halcyonmobile.pagination_extension

import com.halcyonmobile.core.Bar
import com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendLocalStorage

class BarLocalSource(barDao: BarDao) : PagedSuspendLocalStorage<Int, Bar, BarEntity>(barDao){

    override fun valueEntityToValue(valueEntity: BarEntity): Bar = Bar(id = valueEntity.id, title = valueEntity.something)

    override fun valueToValueEntry(value: Bar): BarEntity = BarEntity(id = value.id, something = value.title)

}