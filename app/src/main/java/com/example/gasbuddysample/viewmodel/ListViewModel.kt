package com.example.gasbuddysample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import com.example.gasbuddysample.api.PicsumService
import com.example.gasbuddysample.datasource.ModelDataSourceFactory
import com.example.gasbuddysample.model.NetworkState
import com.example.gasbuddysample.model.PicsumImage


class ListViewModel : ViewModel() {

    private val networkState: LiveData<NetworkState>
    private val imageLiveData: LiveData<PagedList<PicsumImage>>
    private val refreshState: LiveData<NetworkState>

    init {
        val sourceFactory: ModelDataSourceFactory =
            ModelDataSourceFactory(PicsumService.getService(), viewModelScope)
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        imageLiveData = LivePagedListBuilder<String, PicsumImage>(sourceFactory, config).build()
        networkState = Transformations.switchMap(sourceFactory.sourceLiveData)
        { sourceLiveData -> sourceLiveData.networkState };

        refreshState = Transformations.switchMap(sourceFactory.sourceLiveData)
        { sourceLiveData -> sourceLiveData.initialLoad };

    }

    fun refresh() {
        imageLiveData.value?.dataSource?.invalidate()
    }

    fun retry() {
        imageLiveData.value?.retry()
    }


}