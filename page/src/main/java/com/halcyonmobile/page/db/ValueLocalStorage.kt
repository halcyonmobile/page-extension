package com.halcyonmobile.page.db

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
interface ValueLocalStorage<Value>{
    operator fun invoke(values: List<Value>, callback: () -> Unit)
}