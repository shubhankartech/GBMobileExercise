package com.example.gasbuddysample.ui.main.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.gasbuddysample.R
import com.example.gasbuddysample.model.NetworkState
import com.example.gasbuddysample.model.PicsumImage
import com.example.gasbuddysample.viewmodel.DetailsViewModel
import com.example.gasbuddysample.viewmodel.DetailsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment : Fragment() {

    companion object {
        fun newInstance(id: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = id
            return fragment
        }
    }

      private val viewModel:DetailsViewModel by viewModels {
        val id: Int = arguments?.getInt(Intent.EXTRA_TEXT, -1)?:-1
          DetailsViewModelFactory(id, this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.detailsLiveData.observe(this, Observer<PicsumImage> {
            message.text=it.author
            Glide.with(this)
                    .load(it.downloadUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_landscape)
                    .into(detail_image)
        })

        viewModel.statusLiveData.observe(this, Observer<NetworkState> {
            Snackbar.make(details,it.msg?:"Could not display Deails",Snackbar.LENGTH_LONG).show()
        })
    }



}
