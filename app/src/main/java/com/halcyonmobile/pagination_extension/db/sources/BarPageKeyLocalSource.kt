package com.halcyonmobile.pagination_extension.db.sources

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.BarKeyLocalStorage
import com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendKeyLocalStorage
import com.halcyonmobile.page.db.KeyOrEndOfList
import com.halcyonmobile.pagination_extension.PageKeyDao
import com.halcyonmobile.pagination_extension.db.room.KeyEntity

class BarPageKeyLocalSource(private val pagedKeyDao: PageKeyDao) : PagedSuspendKeyLocalStorage<Int, Bar, String, KeyEntity>(
    BAR_PAGE_KEY_ID, pagedKeyDao), BarKeyLocalStorage {

    override fun endToKeyEntity(): KeyEntity =
        KeyEntity(keyEntityId, 0, true)

    override fun keyToKeyEntity(key: Int): KeyEntity =
        KeyEntity(keyEntityId, key.toLong(), false)

    override fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Int> =
        if (keyEntity.isEnd) KeyOrEndOfList.EndReached() else KeyOrEndOfList.Key(keyEntity.value.toInt())

    override suspend fun clear() {
        pagedKeyDao.delete(keyEntityId)
    }

    companion object {
        private const val BAR_PAGE_KEY_ID = "BAR"
    }
}