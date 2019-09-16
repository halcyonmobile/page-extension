package com.halcyonmobile.pagination_extension

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
@Entity
data class BarEntity(@PrimaryKey val id : Int, val something: String)