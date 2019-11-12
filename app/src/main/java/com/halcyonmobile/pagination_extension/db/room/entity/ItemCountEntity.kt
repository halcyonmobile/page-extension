/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.db.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemCountEntity(@PrimaryKey val id: String, val itemCount: Int)