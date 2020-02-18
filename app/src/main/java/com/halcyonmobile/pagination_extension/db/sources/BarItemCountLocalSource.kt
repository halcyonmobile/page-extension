/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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