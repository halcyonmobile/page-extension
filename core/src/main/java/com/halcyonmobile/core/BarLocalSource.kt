package com.halcyonmobile.core

import androidx.paging.DataSource

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
interface BarLocalSource{
    fun get() : DataSource.Factory<Int, Bar>

    suspend fun add(bar: Bar)

    suspend fun addAll(bar: List<Bar>)

    suspend fun delete(bar: Bar)
}