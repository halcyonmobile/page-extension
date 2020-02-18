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
package com.halcyonmobile.pageui.coroutine

import com.halcyonmobile.page.coroutine.PagedResult
import com.halcyonmobile.pageui.MapperBoundaryCallback

/**
 * Helper function to enable mapping your models coming from the dataSource into a UI model
 */
inline fun <Key, Value, NewValue, Error> PagedResult<Key, Value, Error>.map(
    crossinline mapValueToUiValue: (Value) -> NewValue,
    crossinline mapUiValueToValue: (NewValue) -> Value
): PagedResult<Key, NewValue, Error> {
    val newBoundaryCallback = boundaryCallback?.let {
        MapperBoundaryCallback(it, mapUiValueToValue)
    }

    return PagedResult<Key, NewValue, Error>(
        stateChannel = stateChannel,
        dataSourceFactory = dataSourceFactory.map { mapValueToUiValue(it) },
        boundaryCallback = newBoundaryCallback
    )
}