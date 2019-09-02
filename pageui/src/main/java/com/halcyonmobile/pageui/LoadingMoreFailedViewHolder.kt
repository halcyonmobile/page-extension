/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.pageui

import android.view.ViewGroup
import com.halcyonmobile.pageui.databinding.ItemLoadingMoreFailedBinding
import com.halcyonmobile.pageui.R

/**
 * Default ViewHolder for [LoadingMorePagedListAdapter] to handle loading more errors.
 *
 * It has a clickable image which retries with the given action.
 */
class LoadingMoreFailedViewHolder(parent: ViewGroup) : BindingViewHolder<ItemLoadingMoreFailedBinding>(parent, layoutRes) {

    private var retry: (() -> Unit)? = null

    init {
        binding.refreshIcon.setOnClickListener {
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