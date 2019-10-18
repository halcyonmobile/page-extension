/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.pagination_extension

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * [ViewHolder][RecyclerView.ViewHolder] which holds a [binding]
 */
open class BindingViewHolder<T : ViewDataBinding, Data> private constructor(protected val binding: T) :
    RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup, @LayoutRes layoutRes: Int) : this(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutRes,
            parent,
            false
        )
    )

    final fun bind(data: Data){
        bindData(data)
        binding.executePendingBindings()
    }

    protected open fun bindData(data: Data){
    }
}