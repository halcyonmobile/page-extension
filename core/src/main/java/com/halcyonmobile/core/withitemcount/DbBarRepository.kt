/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.core.withitemcount

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.BarDBItemCountLocalStorage
import com.halcyonmobile.core.shared.BarDBKeyLocalStorage
import com.halcyonmobile.core.shared.BarDBLocalSource
import com.halcyonmobile.core.shared.BarRemoteSource
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.core.withitemcount.WithItemCountRepository.Companion.INVALID_ITEM_COUNT
import com.halcyonmobile.page.coroutine.PagedResultWithAdditionalData
import com.halcyonmobile.page.coroutine.createPagedResultWithAdditionalDataFromDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class DbBarRepository(
    private val barRemoteSource: BarRemoteSource,
    private val barLocalSource: BarDBLocalSource,
    private val keyLocalStorage: BarDBKeyLocalStorage,
    private val barItemCountLocalStorage: BarDBItemCountLocalStorage
) : WithItemCountRepository {

    private val itemCountChannel = ConflatedBroadcastChannel<Int>()

    override fun get(coroutineScope: CoroutineScope): PagedResultWithAdditionalData<Int, Bar, Int, NetworkError> =
        createPagedResultWithAdditionalDataFromDao(
            coroutineScope = coroutineScope,
            networkPageSize = 10,
            keyLocalStorage = keyLocalStorage,
            valueLocalStorage = barLocalSource,
            additionalDataLocalStorage = barItemCountLocalStorage,
            request = barRemoteSource::getWithItemCount,
            additionalDataChannel = itemCountChannel,
            initialPageKey = 0
        )

    override suspend fun fetch() {
        barItemCountLocalStorage.clear()
        itemCountChannel.offer(INVALID_ITEM_COUNT)
        keyLocalStorage.clear()
        barLocalSource.clear()
    }
}