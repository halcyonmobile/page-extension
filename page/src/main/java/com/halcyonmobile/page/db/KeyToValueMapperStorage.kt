package com.halcyonmobile.page.db

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
abstract class KeyToValueMapperStorage<Key, Value> : KeyLocalStorage<Key, Value> {
    private var didReachEnd = false

    override fun cache(key: KeyOrEndOfList<Key>, callback: () -> Unit) {
        didReachEnd = key is KeyOrEndOfList.EndReached
    }

    override fun getKey(value: Value, callback: (KeyOrEndOfList<Key>) -> Unit) {
        if (didReachEnd) {
            callback(KeyOrEndOfList.EndReached())
        } else {
            callback(KeyOrEndOfList.Key(mapValueToKey(value)))
        }
    }

    abstract fun mapValueToKey(value: Value): Key
}

inline fun <Key, Value> KeyToValueMapperStorage(crossinline valueToKey: (Value) -> Key): KeyToValueMapperStorage<Key, Value> =
    object : KeyToValueMapperStorage<Key, Value>() {
        override fun mapValueToKey(value: Value): Key = valueToKey(value)
    }