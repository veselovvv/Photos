package com.veselovvv.photos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PhotosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        if (savedInstanceState == null) { // if fragment container is empty
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, PhotosFragment.newInstance())
                .commit()
        }
    }
}