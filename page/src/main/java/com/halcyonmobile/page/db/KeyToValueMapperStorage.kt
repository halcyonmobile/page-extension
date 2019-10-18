package com.halcyonmobile.page.db

/**
 * An abstract implementation of [KeyLocalStorage] which assumes the page [Key] can be extracted from the last data value.
 */
abstract class KeyToValueMapperStorage<Key, Value> : KeyLocalStorage<Key, Value> {
    private var didReachEnd = false

    final override fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit) {
        didReachEnd = key is KeyOrEndOfList.EndReached
        callback()
    }

    final override fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit) {
        if (didReachEnd) {
            callback(KeyOrEndOfList.EndReached())
        } else {
            callback(KeyOrEndOfList.Key(mapValueToKey(value)))
        }
    }

    /**
     * Extracts the [Key] from the [value]
     */
    abstract fun mapValueToKey(value: Value): Key
}

inline fun <Key, Value> KeyToValueMapperStorage(crossinline valueToKey: (Value) -> Key): KeyToValueMapperStorage<Key, Value> =
    object : KeyToValueMapperStorage<Key, Value>() {
        override fun mapValueToKey(value: Value): Key = valueToKey(value)
    }