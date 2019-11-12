/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.db.sources

import com.halcyonmobile.core.shared.BarDBItemCountLocalStorage
import com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendAdditionalDataLocalStorage
import com.halcyonmobile.pagination_extension.db.room.dao.ItemCountDao
import com.halcyonmobile.pagination_extension.db.room.entity.ItemCountEntity

class BarItemCountLocalSource(private val itemCountDao: ItemCountDao) : PagedSuspendAdditionalDataLocalStorage<Int, String, ItemCountEntity>("BAR_ITEM_COUNT", itemCountDao),
    BarDBItemCountLocalStorage {

    override suspend fun clear() = itemCountDao.delete(additionalDataKey)

    override fun mapEntityToValue(additionalDataEntity: ItemCountEntity): Int = additionalDataEntity.itemCount

    override fun mapValueToEntity(additionalData: Int): ItemCountEntity = ItemCountEntity(additionalDataKey, additionalData)

}