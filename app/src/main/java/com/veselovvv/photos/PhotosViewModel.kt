package com.veselovvv.photos

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class PhotosViewModel : ViewModel() {
    private val photoLiveData = FlickrFetchr().fetchImages()

    fun observe(owner: LifecycleOwner, observer: Observer<List<Photo>>) =
        photoLiveData.observe(owner, observer)
}