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

interface SuspendKeyLocalStorage<Key, Value> {
    suspend fun cache(key: KeyOrEndOfList<Key>)

    suspend fun getKey(value: Value): KeyOrEndOfList<Key>
}

class SuspendKeyLocalStorageAdapter<Key, Value>(
    private val coroutineScope: CoroutineScope,
    private val suspendKeyLocalStorage: SuspendKeyLocalStorage<Key, Value>
) : KeyLocalStorage<Key, Value> {

    override fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit) {
        coroutineScope.launch {
            suspendKeyLocalStorage.cache(key)
            callback()
        }
    }

    override fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit) {
        coroutineScope.launch {
            callback(suspendKeyLocalStorage.getKey(value))
        }
    }

}

abstract class PagedSuspendKeyLocalStorage<Key, Value, KeyEntityId, KeyEntity>(
    private val keyEntityId: KeyEntityId,
    private val keyDao: SuspendKeyDao<KeyEntityId, KeyEntity>
) : SuspendKeyLocalStorage<Key, Value> {

    override suspend fun cache(key: KeyOrEndOfList<Key>) {
        val entity = if (key is KeyOrEndOfList.Key<Key>){
            keyToKeyEntity(key.key)
        } else {
            endToKeyEntity()
        }
        keyDao.insert(entity)
    }

    override suspend fun getKey(value: Value): KeyOrEndOfList<Key> =
        keyEntityToKey(keyDao.get(keyEntityId))

    abstract fun endToKeyEntity() : KeyEntity

    abstract fun keyToKeyEntity(key: Key): KeyEntity

    abstract fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Key>
}