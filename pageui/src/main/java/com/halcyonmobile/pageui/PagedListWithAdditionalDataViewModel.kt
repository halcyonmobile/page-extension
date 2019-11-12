/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pageui

import androidx.lifecycle.LiveData

/**
 * Extended interface for ViewModels which provides paginated data and an additinal data.
 */
interface PagedListWithAdditionalDataViewModel<Value, AdditionalData, Error> : PagedListViewModel<Value, Error>{
    val additionalData: LiveData<AdditionalData>
}