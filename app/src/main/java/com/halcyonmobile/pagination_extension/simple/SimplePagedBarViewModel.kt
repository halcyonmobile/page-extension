/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.simple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.core.simple.SimpleRepository
import com.halcyonmobile.pageui.PagedListViewModel
import com.halcyonmobile.pageui.coroutine.PagedListViewModelDelegate
import kotlinx.coroutines.launch


/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class SimplePagedBarViewModel(
    private val repository: SimpleRepository,
    private val delegate: PagedListViewModelDelegate<Int, Bar, NetworkError>
) : ViewModel(),
    PagedListViewModel<Bar, NetworkError> by delegate {


    constructor(repository: SimpleRepository) : this(repository, PagedListViewModelDelegate<Int, Bar, NetworkError>())

    fun loadData() {
        viewModelScope.launch {
            delegate.setupPagedListByRequest(10, 10) {
                repository.get(viewModelScope)
            }
        }
    }

    fun onForceRefresh() {
        viewModelScope.launch {
            repository.fetch()
        }
    }
}