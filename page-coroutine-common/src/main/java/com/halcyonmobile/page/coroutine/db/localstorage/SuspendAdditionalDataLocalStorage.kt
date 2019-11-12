/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.page.coroutine.db.localstorage

/**
 * Simple interface defining how to access additional data for the page-data.
 */
interface SuspendAdditionalDataLocalStorage<AdditionalData>{

    /**
     * Returns the additional data stored for the paged data
     */
    suspend fun get() : AdditionalData?

    /**
     * Caches the additional data for the paged data
     */
    suspend fun cache(additionalData: AdditionalData)
}