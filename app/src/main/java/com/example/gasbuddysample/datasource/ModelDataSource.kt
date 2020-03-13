package com.example.gasbuddysample.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.gasbuddysample.api.PicsumService
import com.example.gasbuddysample.model.NetworkState
import com.example.gasbuddysample.model.PicsumImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModelDataSource(
    private val picsumService: PicsumService,
    private val scope: CoroutineScope
) : PageKeyedDataSource<String, PicsumImage>() {

    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()


    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PicsumImage>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    picsumService.fetchPosts(
                        page = "1",
                        limit = params.requestedLoadSize

                    )
                }
                when {
                    response.isSuccessful -> {
                        val listing = response.body()?: emptyList()
                        val pageHeader: String? = response.headers().get("link")
                        val pageDetails: Map<String, String?>? = getPageDetails(pageHeader)
                        val firstIndex = pageDetails?.get("prev")
                        val lastIndex = pageDetails?.get("next")
                        callback.onResult(listing ,firstIndex, lastIndex)
                        networkState.postValue(NetworkState.LOADED)
                        initialLoad.postValue(NetworkState.LOADED)
                    }
                    !response.isSuccessful -> {
                        retry = {
                            loadInitial(params, callback)
                        }

                        networkState.postValue(NetworkState.
                            error("Failed to fetch initial list"+response.errorBody().toString() ))
                        initialLoad.postValue(NetworkState.
                            error("Failed to fetch initial list"+response.errorBody().toString()))
                    }
                }

            } catch (exception: Exception) {
                val error = NetworkState.error(exception.message ?: "unknown error")
                networkState.postValue(error)
                initialLoad.postValue(error)
            }
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, PicsumImage>
    ) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            try {
                val response = picsumService.fetchPosts(
                    page = params.key,
                    limit = params.requestedLoadSize
                )
                when {
                    response.isSuccessful -> {
                        val listing = response.body()?: emptyList()
                        val pageHeader: String? = response.headers().get("link")
                        val pageDetails: Map<String, String?>? = getPageDetails(pageHeader)
                        val lastIndex = pageDetails?.get("next")
                        callback.onResult(listing ?: listOf(), lastIndex)
                        networkState.postValue(NetworkState.LOADED)
                    }
                    !response.isSuccessful -> {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(
                            // showing appropriate message based on error not shown
                            NetworkState.error("error : ${response.errorBody()}")

                        )
                    }
                }

            } catch (exception: Exception) {
                Log.e("ModelDataSource", "Failed to fetch data!")
                networkState.postValue(NetworkState.error("Failed to fetch data!"))
            }
        }
    }


    private fun getPageDetails(pageHeader: String?): Map<String, String?>? {
        val splits: List<String>? = pageHeader?.split(",")

        return splits?.associateBy(keySelector = {
            when {
                it.contains("rel=\"next\"") -> "next"
                it.contains("rel=\"prev\"") -> "prev"
                else -> ""
            }
        },valueTransform = {
            extract(it)
        })


    }

    private fun extract(split: String): String? {
        val beginIndex: Int = split.indexOf('=')
        val endIndex: Int = split.lastIndexOf('&')
        if(endIndex == -1 || endIndex >=beginIndex) return null
        return split.substring(beginIndex + 1, endIndex)

    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, PicsumImage>
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
                it.invoke()
        }
    }


}