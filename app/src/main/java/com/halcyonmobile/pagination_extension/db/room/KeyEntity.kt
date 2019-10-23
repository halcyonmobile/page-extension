package com.halcyonmobile.pagination_extension.db.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KeyEntity(@PrimaryKey val id: String, val value: Long, val isEnd: Boolean)