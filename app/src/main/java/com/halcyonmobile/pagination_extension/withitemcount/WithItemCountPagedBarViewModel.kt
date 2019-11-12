/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.NetworkError
import com.halcyonmobile.core.withitemcount.WithItemCountRepository
import com.halcyonmobile.core.withitemcount.WithItemCountRepository.Companion.INVALID_ITEM_COUNT
import com.halcyonmobile.pageui.PagedListViewModel
import com.halcyonmobile.pageui.coroutine.PagedListWithAdditionalDataViewModelDelegate
import kotlinx.coroutines.launch

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
class WithItemCountPagedBarViewModel(
    private val repository: WithItemCountRepository,
    private val delegate: PagedListWithAdditionalDataViewModelDelegate<Int, Bar, Int, NetworkError>
) : ViewModel(),
    PagedListViewModel<Bar, NetworkError> by delegate {

    constructor(repository: WithItemCountRepository) : this(repository, PagedListWithAdditionalDataViewModelDelegate<Int, Bar, Int, NetworkError>())

    val itemCount: LiveData<Int> = Transformations.map(delegate.additionalData) { it.takeUnless { it == INVALID_ITEM_COUNT } }

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