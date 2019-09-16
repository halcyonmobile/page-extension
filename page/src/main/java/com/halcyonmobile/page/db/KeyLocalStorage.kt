package com.halcyonmobile.page.db

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
interface KeyLocalStorage<Key, Value> {
    fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit)

    fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit)
}