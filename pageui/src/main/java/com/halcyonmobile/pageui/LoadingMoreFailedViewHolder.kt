/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pageui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

/**
 * Default ViewHolder for [LoadingMorePagedListAdapter] to handle loading more errors.
 *
 * It has a clickable image which retries with the given action.
 */
class LoadingMoreFailedViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)){

    private var retry: (() -> Unit)? = null

    init {
        itemView.findViewById<ImageView>(R.id.refresh_icon).setOnClickListener {
            retry?.invoke()
        }
    }

    fun bind(retry: () -> Unit) {
        this.retry = retry
    }

    companion object {
        val layoutRes = R.layout.item_loading_more_failed
    }
}