package com.example.gasbuddysample.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gasbuddysample.R
import com.example.gasbuddysample.adapter.ListAdapter
import com.example.gasbuddysample.model.NetworkState
import com.example.gasbuddysample.model.PicsumImage
import com.example.gasbuddysample.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: ListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        initializeAdapter()
    }

    private fun initializeAdapter() {
        val glide = Glide.with(this)
        val adapter = ListAdapter(glide){
            viewModel.retry()}
        list.adapter = adapter

        viewModel.imageLiveData.observe(this, Observer<PagedList<PicsumImage>> {
            adapter.submitList(it) {
                val layoutManager = (list.layoutManager as LinearLayoutManager)
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    list.scrollToPosition(position)
                }
            }
        })

        viewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })

        viewModel.refreshState.observe(this, Observer {
            swipe_to_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_to_refresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

}
