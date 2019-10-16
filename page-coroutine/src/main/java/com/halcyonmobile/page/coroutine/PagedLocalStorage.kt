package com.halcyonmobile.page.coroutine

import androidx.paging.DataSource
import com.halcyonmobile.page.db.ValueFactoryProvider
import com.halcyonmobile.page.db.ValueLocalCacher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */

interface SuspendValueLocalStorage<Key, Value> : ValueFactoryProvider<Key, Value> {

    suspend fun cache(values: List<Value>)
}

abstract class PagedSuspendLocalStorage<Key,Value,ValueEntity>(protected val pagedDao : SuspendPagedDao<Key, Value, ValueEntity>) : SuspendValueLocalStorage<Key,Value> {

    override fun getValueFactory(): DataSource.Factory<Key, Value> = pagedDao.getValueEntitiesFactory().map(::valueEntityToValue)

    override suspend fun cache(values: List<Value>) = pagedDao.insert(values.map(::valueToValueEntry))

    abstract fun valueToValueEntry(value: Value): ValueEntity

    abstract fun valueEntityToValue(valueEntity: ValueEntity): Value
}

class SuspendValueLocalCacherAdapter<Key, Value>(
    private val coroutineScope: CoroutineScope,
    private val suspendValueLocalStorage: SuspendValueLocalStorage<Key, Value>
) : ValueLocalCacher<Key, Value> {

    override fun cache(values: List<Value>, callback: () -> Unit) {
        coroutineScope.launch {
            suspendValueLocalStorage.cache(values)
            callback()
        }
    }
}