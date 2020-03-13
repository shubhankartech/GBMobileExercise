package com.example.gasbuddysample.adapter

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.gasbuddysample.model.PicsumImage
import android.view.ViewGroup
import com.example.gasbuddysample.R
import com.example.gasbuddysample.model.NetworkState
import com.example.gasbuddysample.viewholder.ImageItemViewHolder
import com.example.gasbuddysample.viewholder.NetworkStatusViewHolder


class ListAdapter(
    private val glide: RequestManager,
    private val retryCallback: () -> Unit
) : PagedListAdapter<PicsumImage, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.list_item -> (holder as ImageItemViewHolder).bind(getItem(position))
            R.layout.network_item -> (holder as NetworkStatusViewHolder).bindTo(
                networkState)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            (holder as ImageItemViewHolder).update(payloads,item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.list_item -> ImageItemViewHolder.create(parent, glide)
            R.layout.network_item -> NetworkStatusViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_item
        } else {
            R.layout.list_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }


    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<PicsumImage>() {
            override fun areContentsTheSame(oldItem: PicsumImage, newItem: PicsumImage): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: PicsumImage, newItem: PicsumImage): Boolean =
                oldItem.id == newItem.id

            override fun getChangePayload(oldItem: PicsumImage, newItem: PicsumImage): Any? {
                val set = mutableSetOf<String>()
                if (oldItem.author != newItem.author) {
                    set.add("author")
                }
                if (oldItem.downloadUrl != newItem.downloadUrl) {
                    set.add("downloadUrl")
                }


                return set
            }
        }
    }
}