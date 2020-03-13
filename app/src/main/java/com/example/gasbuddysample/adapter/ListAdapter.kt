package com.example.gasbuddysample.adapter

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.gasbuddysample.model.PicsumImage
import android.os.Bundle
import android.view.ViewGroup
import com.example.gasbuddysample.model.NetworkState


class ListAdapter(
    private val glide: RequestManager
) : PagedListAdapter<PicsumImage, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
//            R.layout.reddit_post_item -> (holder as RedditPostViewHolder).bind(getItem(position))
//            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
//                networkState)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            (holder as ImagePostViewHolder).updateScore(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            R.layout.reddit_post_item -> RedditPostViewHolder.create(parent, glide)
//            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
//        return if (hasExtraRow() && position == itemCount - 1) {
//            R.layout.network_state_item
//        } else {
//            R.layout.reddit_post_item
//        }
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
                val diff = Bundle()
                if (oldItem.author != newItem.author) {
                    diff.putString("author", newItem.author)
                }
                if (oldItem.downloadUrl != newItem.downloadUrl) {
                    diff.putString("downloadUrl", newItem.downloadUrl)
                }

                if (diff.size() == 0) {
                    return null
                }
                return diff
            }
        }
    }
}