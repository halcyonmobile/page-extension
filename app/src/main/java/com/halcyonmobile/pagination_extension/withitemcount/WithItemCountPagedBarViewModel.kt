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