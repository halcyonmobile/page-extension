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
package com.halcyonmobile.page

/**
 * Listener to the state updates of a [StateProvidingListDataSource]
 */
interface DataSourceUpdateListener<Error> {
    operator fun invoke(state: DataSourceState<Error>)
}