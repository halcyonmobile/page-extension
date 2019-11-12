/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page.coroutine.db.localstorage

import androidx.paging.DataSource
import com.halcyonmobile.page.coroutine.db.dao.SuspendPagedDao

/**
 * Basic implementation of the LocalSource to store the values for a paged data.
 */
abstract class PagedSuspendLocalStorage<Key,Value,ValueEntity>(protected val pagedDao : SuspendPagedDao<Key, Value, ValueEntity>) :
    SuspendValueLocalStorage<Key, Value> {

    final override fun getValueFactory(): DataSource.Factory<Key, Value> = pagedDao.getValueEntitiesFactory().map(::valueEntityToValue)

    final override suspend fun cache(values: List<Value>) = pagedDao.insert(values.map(::valueToValueEntry))

    /**
     * Converts a [value] into a [ValueEntity] that can be stored in [pagedDao]
     */
    abstract fun valueToValueEntry(value: Value): ValueEntity

    /**
     * Converts the [valueEntity] to a [Value] which can be used in the repository.
     */
    abstract fun valueEntityToValue(valueEntity: ValueEntity): Value
}