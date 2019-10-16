package com.halcyonmobile.page.db

import androidx.paging.DataSource

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
interface ValueLocalCacher<Key, Value>{

    fun cache(values: List<Value>, callback: () -> Unit)
}