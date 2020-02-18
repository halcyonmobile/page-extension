/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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