package com.halcyonmobile.pagination_extension

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.BarRemoteSource
import com.halcyonmobile.core.BarRepository
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
class BarViewModel(private val delegate: PagedListViewModelDelegate<Int, Bar, NetworkError>) : ViewModel(),
    PagedListViewModel<Bar, NetworkError> by delegate {

    constructor() : this(PagedListViewModelDelegate<Int, Bar, NetworkError>())

    lateinit var context: Context
    private val repository by lazy { BarRepository(Room.databaseBuilder(context, BarDataBase::class.java, "BarDataBase").build().barDao, BarRemoteSource()) }


    fun a() {
        viewModelScope.launch {
            delegate.setupPagedListByRequest(20, 40) {
                repository.get(viewModelScope)
            }
        }
    }

    fun onForceRefresh() {
//        repository.fetch()
    }
}