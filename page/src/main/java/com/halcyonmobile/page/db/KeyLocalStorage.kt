package com.halcyonmobile.page.db

/**
 * Interface representing the LocalStorage of the key used for pages of data.
 *
 * The key either can be obtained from the last data element [Value] or stored somewhere.
 * If storage is prefered, calling in cache should result in that saving and getKey should result in it's loading
 * If it can be extracted from the last data element, then cache should simply call the callback instantly.
 *
 * Used by [StateProvidingBoundaryCallBack]
 */
interface KeyLocalStorage<Key, Value> {

    /**
     * Called when a new [key] is received from the network.
     * Should call [callback] when it's finished saving the [key]
     */
    fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit)

    /**
     * Called when the end of the local data is reached and the next key is necessary.
     * [value] is the last value of the data.
     */
    fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit)
}