/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
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