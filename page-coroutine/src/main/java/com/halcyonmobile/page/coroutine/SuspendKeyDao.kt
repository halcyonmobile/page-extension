package com.halcyonmobile.page.coroutine

import com.halcyonmobile.page.db.KeyOrEndOfList

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
interface SuspendKeyDao<Key, KeyEntity> {
    suspend fun get(): KeyEntity

    suspend fun insert(keyEntity: KeyEntity)

    fun keyToKeyEntity(key: KeyOrEndOfList<Key>): KeyEntity

    fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Key>
}