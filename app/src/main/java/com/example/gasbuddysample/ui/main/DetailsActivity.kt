package com.example.gasbuddysample.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.gasbuddysample.R
import com.example.gasbuddysample.ui.main.ui.details.DetailsFragment

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)
        val id:Int? = intent.getIntExtra(Intent.EXTRA_TEXT,-1)
        val bundle = bundleOf(
            Intent.EXTRA_TEXT to id
        )
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailsFragment.newInstance(bundle))
                .commitNow()
        }
    }

}
