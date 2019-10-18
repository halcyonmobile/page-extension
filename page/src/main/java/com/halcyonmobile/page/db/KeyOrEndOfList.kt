package com.halcyonmobile.page.db

/**
 * Represents the Key of the page.
 *
 * It can be either that no more data is available on the network [EndReached] or a value [Key]
 */
sealed class KeyOrEndOfList<Key> {
    class EndReached<Key> : KeyOrEndOfList<Key>()
    data class Key<Key>(val key: Key) : KeyOrEndOfList<Key>()
}