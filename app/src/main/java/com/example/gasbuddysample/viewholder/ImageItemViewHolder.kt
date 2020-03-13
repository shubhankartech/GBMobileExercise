package com.example.gasbuddysample.viewholder

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.gasbuddysample.R
import com.example.gasbuddysample.model.PicsumImage

class ImageItemViewHolder(view: View, private val glide: RequestManager)
    : RecyclerView.ViewHolder(view) {
    private val author: TextView = view.findViewById(R.id.author)
    private val thumbnail : ImageView = view.findViewById(R.id.image)
    private var item : PicsumImage? = null
    init {
        view.setOnClickListener {
            item?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(post: PicsumImage?) {
        this.item = post
        author.text = post?.author ?: "loading"
        if (post?.downloadUrl?.startsWith("http") == true) {
            thumbnail.visibility = View.VISIBLE
            glide.load(post.downloadUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_landscape)
                .into(thumbnail)
        } else {
            thumbnail.visibility = View.GONE
            glide.clear(thumbnail)
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): ImageItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
            return ImageItemViewHolder(view, glide)
        }
    }

    fun update(
        payload: MutableList<Any>,
        picsumImage: PicsumImage?
    ) {
        val set = payload?.firstOrNull() as Set<String>?
        set?.forEach {
            when(it) {
                "author" -> {
                    author.text = picsumImage?.author
                }
                "downloadUrl" -> {
                    val downloadUrl = picsumImage?.downloadUrl
                    if (downloadUrl?.startsWith("http") == true) {
                        thumbnail.visibility = View.VISIBLE
                        glide.load(downloadUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_landscape)
                            .into(thumbnail)
                    }
                }
            }
        }


    }
}