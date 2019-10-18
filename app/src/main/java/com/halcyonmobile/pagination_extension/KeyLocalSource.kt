package com.halcyonmobile.pagination_extension

import com.halcyonmobile.core.Bar
import com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendKeyLocalStorage
import com.halcyonmobile.page.db.KeyOrEndOfList

class BarPageKeyLocalSource(pagedKeyDao: PageKeyDao) : PagedSuspendKeyLocalStorage<Int, Bar, String, KeyEntity>(BAR_PAGE_KEY_ID, pagedKeyDao) {

    override fun endToKeyEntity(): KeyEntity =
        KeyEntity(keyEntityId, 0, true)

    override fun keyToKeyEntity(key: Int): KeyEntity =
        KeyEntity(keyEntityId, key.toLong(), false)

    override fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Int> =
        if (keyEntity.isEnd) KeyOrEndOfList.EndReached() else KeyOrEndOfList.Key(keyEntity.value.toInt())

    companion object {
        private const val BAR_PAGE_KEY_ID = "BAR"
    }
}