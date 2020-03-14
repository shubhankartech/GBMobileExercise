package com.example.gasbuddysample.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gasbuddysample.api.PicsumService
import com.example.gasbuddysample.model.NetworkState
import com.example.gasbuddysample.model.PicsumImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailsViewModel(
        private var id: Int,
        savedStateHandle: SavedStateHandle
) : ViewModel() {

     companion object {
         const val KEY = "key_id"
     }

     var detailsLiveData = MutableLiveData<PicsumImage>()

    var statusLiveData = MutableLiveData<NetworkState>()


    init {
         if (!savedStateHandle.contains(KEY)) {
             savedStateHandle.set(KEY, id)
         }
         id = savedStateHandle.get<Int>(KEY)?:-1

         initializeLiveData(id)
     }

     private fun initializeLiveData(id: Int) {
        detailsLiveData = MutableLiveData<PicsumImage>()
         viewModelScope.launch(Dispatchers.IO){
             val response = PicsumService.getService().getImageDetail(id)

             when {
                 response.isSuccessful -> {
                     val listing = response.body()
                     detailsLiveData.postValue(listing)
                 }
                 !response.isSuccessful -> {
                    statusLiveData.postValue(NetworkState.error("Failed to get details"))
                 }
             }
         }
     }


 }
