package com.halcyonmobile.page.coroutine

import androidx.paging.DataSource
import com.halcyonmobile.page.db.ValueLocalStorage
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
class PagedLocalStorage<Key, Value, ValueEntity>(
    private val coroutineScope: CoroutineScope,
    private val pagedDao: SuspendPagedDao<Key, Value, ValueEntity>
) : ValueLocalStorage<Value> {

    fun getValueFactory(): DataSource.Factory<Key, Value> =
        pagedDao.getValueEntitiesFactory().map(pagedDao::valueEntityToValue)

    override fun invoke(values: List<Value>, callback: () -> Unit) {
        coroutineScope.launch {
            pagedDao.insert(values.map(pagedDao::valueToValueEntry))
        }
    }
}