package com.halcyonmobile.pagination_extension

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KeyEntity(@PrimaryKey val id: String, val value: Long, val isEnd: Boolean)