package com.example.gasbuddysample.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class DetailsViewModelFactory(
        private val id:Int ,
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null

) : AbstractSavedStateViewModelFactory(owner,defaultArgs) {
    override fun <T : ViewModel> create(
            idKey: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
    ) : T {
        return DetailsViewModel(id, handle) as T
    }

}
