package com.halcyonmobile.core

import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.page.coroutine.createPagedResultFromDao
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendKeyLocalStorage
import com.halcyonmobile.page.coroutine.db.localstorage.SuspendValueLocalStorage
import kotlinx.coroutines.CoroutineScope

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class BarRepository(
    private val barRemoteSource: BarRemoteSource,
    private val barLocalSource: SuspendValueLocalStorage<Int, Bar>,
    private val keyLocalStorage: SuspendKeyLocalStorage<Int, Bar>
) {

    fun get(coroutineScope: CoroutineScope): PagedResult<Int, Bar, NetworkError> =
        createPagedResultFromDao(
            coroutineScope = coroutineScope,
            networkPageSize = 20,
            keyLocalStorage = keyLocalStorage,
            valueLocalStorage = barLocalSource,
            request = barRemoteSource::get,
            initialPageKey = 0
        )
}