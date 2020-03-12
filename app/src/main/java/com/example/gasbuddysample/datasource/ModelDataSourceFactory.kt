package com.example.gasbuddysample.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.gasbuddysample.api.PicsumService
import com.example.gasbuddysample.model.PicsumImage
import kotlinx.coroutines.CoroutineScope

class ModelDataSourceFactory(private val picsumService: PicsumService,
                             private val scope: CoroutineScope
) : DataSource.Factory<String, PicsumImage>() {

    val sourceLiveData = MutableLiveData<ModelDataSource>()

    override fun create(): DataSource<String, PicsumImage> {

        val source = ModelDataSource(picsumService, scope)
        sourceLiveData.postValue(source)
        return source
    }
}