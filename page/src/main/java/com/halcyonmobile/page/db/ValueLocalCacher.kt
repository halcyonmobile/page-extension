package com.halcyonmobile.page.db

/**
 * Simple interface which defines a class which saves data in the db.
 *
 * Used by [StateProvidingBoundaryCallBack]
 */
interface ValueLocalCacher<Key, Value> {

    fun cache(values: List<Value>, callback: () -> Unit)
}