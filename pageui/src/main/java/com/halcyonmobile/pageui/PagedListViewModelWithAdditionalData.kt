/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pageui

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

interface PagedListViewModelWithAdditionalData<Data, AdditionalData, Error>  : PagedListViewModel<Data, Error>{
    val additinalData: LiveData<PagedList<AdditionalData>>
}