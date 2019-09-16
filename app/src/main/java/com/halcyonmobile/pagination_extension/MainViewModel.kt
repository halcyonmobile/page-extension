package com.halcyonmobile.pagination_extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halcyonmobile.core.FooRemoteSource
import com.halcyonmobile.core.FooRepository
import com.halcyonmobile.core.NetworkError
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
class MainViewModel(delegate : PagedListViewModelDelegate<Int, Int, NetworkError>) : ViewModel(),
    PagedListViewModel<Int, NetworkError> by delegate {

    constructor() : this(PagedListViewModelDelegate<Int, Int, NetworkError>())

    private val repository = FooRepository(FooRemoteSource())


    init {
        viewModelScope.launch{
            delegate.setupPagedListByRequest {
                repository.get(viewModelScope)
            }
        }
    }

    fun onForceRefresh() {
        repository.fetch()
    }
}