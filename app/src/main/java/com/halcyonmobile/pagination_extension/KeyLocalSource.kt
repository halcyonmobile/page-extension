package com.halcyonmobile.pagination_extension

import com.halcyonmobile.core.Bar
import com.halcyonmobile.page.coroutine.PagedSuspendKeyLocalStorage
import com.halcyonmobile.page.db.KeyOrEndOfList

class BarPageKeyLocalSource(pagedKeyDao: PageKeyDao) : PagedSuspendKeyLocalStorage<Int, Bar, String, KeyEntity>(BAR_PAGE_KEY_ID, pagedKeyDao) {

    override fun endToKeyEntity(): KeyEntity =
        KeyEntity(BAR_PAGE_KEY_ID, 0, true)

    override fun keyToKeyEntity(key: Int): KeyEntity =
        KeyEntity(BAR_PAGE_KEY_ID, key.toLong(), false)

    override fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Int> =
        if (keyEntity.isEnd) KeyOrEndOfList.EndReached() else KeyOrEndOfList.Key(keyEntity.value.toInt())

    companion object {
        private const val BAR_PAGE_KEY_ID = "BAR"
    }
}