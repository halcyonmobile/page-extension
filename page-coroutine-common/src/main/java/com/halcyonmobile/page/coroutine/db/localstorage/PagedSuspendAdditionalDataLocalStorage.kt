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
package com.halcyonmobile.page.coroutine.db.localstorage

import com.halcyonmobile.page.coroutine.db.dao.SuspendAdditionalDataDao

abstract class PagedSuspendAdditionalDataLocalStorage<AdditionalData, AdditionalDataEntityKey, AdditionalDataEntity>(
    protected val additionalDataKey: AdditionalDataEntityKey,
    private val additionalDataDao: SuspendAdditionalDataDao<AdditionalDataEntityKey, AdditionalDataEntity>
) : SuspendAdditionalDataLocalStorage<AdditionalData> {

    final override suspend fun get(): AdditionalData? =
        try {
            mapEntityToValue(additionalDataDao.get(additionalDataKey))
        } catch (throwable: Throwable){
            null
        }

    final override suspend fun cache(additionalData: AdditionalData) {
        additionalDataDao.insert(mapValueToEntity(additionalData))
    }

    abstract fun mapEntityToValue(additionalDataEntity: AdditionalDataEntity) : AdditionalData

    abstract fun mapValueToEntity(additionalData: AdditionalData) : AdditionalDataEntity

}