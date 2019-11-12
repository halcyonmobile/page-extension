/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pageui

import androidx.paging.PagedList

/**
 * A boundary Callback which helps mapping models to another model.
 *
 * It used a delegated [boundaryCallback] and maps the models so it can use it.
 */
abstract class MapperBoundaryCallback<T, R>(private val boundaryCallback: PagedList.BoundaryCallback<R>) : PagedList.BoundaryCallback<T>() {
    override fun onItemAtEndLoaded(itemAtEnd: T) {
        boundaryCallback.onItemAtEndLoaded(map(itemAtEnd))
    }

    override fun onItemAtFrontLoaded(itemAtFront: T) {
        boundaryCallback.onItemAtFrontLoaded(map(itemAtFront))
    }

    override fun onZeroItemsLoaded() {
        boundaryCallback.onZeroItemsLoaded()
    }

    abstract fun map(value: T): R

    companion object {
        inline operator fun <T, R> invoke(boundaryCallback: PagedList.BoundaryCallback<T>, crossinline mapper: (R) -> T): PagedList.BoundaryCallback<R> =
            object : MapperBoundaryCallback<R, T>(boundaryCallback) {
                override fun map(value: R): T = mapper(value)
            }
    }
}