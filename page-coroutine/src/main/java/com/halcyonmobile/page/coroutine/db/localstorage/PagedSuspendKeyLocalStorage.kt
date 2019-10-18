package com.halcyonmobile.page.coroutine.db.localstorage

import com.halcyonmobile.page.coroutine.db.dao.SuspendKeyDao
import com.halcyonmobile.page.db.KeyOrEndOfList

/**
 * Basic implementation of the LocalSource to store the paged requests' page-keys.
 *
 * It's assumed that every keys for paged requests are contained in the same table, with different key,
 * so each request would have a key, which identifies that paged-request's page key.
 */
abstract class PagedSuspendKeyLocalStorage<Key, Value, KeyEntityId, KeyEntity>(
    protected val keyEntityId: KeyEntityId,
    protected val keyDao: SuspendKeyDao<KeyEntityId, KeyEntity>
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

    /**
     * Converts [KeyOrEndOfList.EndReached] to a [KeyEntity] that can be stored in the [keyDao]
     */
    abstract fun endToKeyEntity() : KeyEntity

    /**
     * Converts the key hold in [KeyOrEndOfList.Key] into a [KeyEntity] that can be stored in the [keyDao]
     */
    abstract fun keyToKeyEntity(key: Key): KeyEntity

    /**
     * Converts the [keyEntity] into a [KeyOrEndOfList] which can be returned for the repository.
     */
    abstract fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Key>
}