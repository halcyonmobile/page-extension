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