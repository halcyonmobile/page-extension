package com.halcyonmobile.page.coroutine

import com.halcyonmobile.page.db.KeyLocalStorage
import com.halcyonmobile.page.db.KeyOrEndOfList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class PagedKeyLocalStorage<Key, Value, KeyEntity>(
    private val coroutineScope: CoroutineScope,
    private val keyDao: SuspendKeyDao<Key, KeyEntity>
) : KeyLocalStorage<Key, Value> {
    override fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit) {
        coroutineScope.launch {
            keyDao.insert(keyDao.keyToKeyEntity(key))
            callback()
        }
    }

    override fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit) {
        coroutineScope.launch {
            callback(keyDao.keyEntityToKey(keyDao.get()))
        }
    }

}