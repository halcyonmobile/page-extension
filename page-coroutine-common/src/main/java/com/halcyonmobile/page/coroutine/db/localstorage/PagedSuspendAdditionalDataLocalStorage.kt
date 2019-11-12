/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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