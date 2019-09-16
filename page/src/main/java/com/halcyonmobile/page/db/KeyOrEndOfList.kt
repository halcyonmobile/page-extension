package com.halcyonmobile.page.db

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
sealed class KeyOrEndOfList<Key> {
    class EndReached<Key> : KeyOrEndOfList<Key>()
    data class Key<Key>(val key: Key) : KeyOrEndOfList<Key>()
}