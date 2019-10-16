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
interface SuspendKeyDao<KeyEntityId, KeyEntity> {
    suspend fun get(id: KeyEntityId): KeyEntity

    suspend fun insert(keyEntity: KeyEntity)
}