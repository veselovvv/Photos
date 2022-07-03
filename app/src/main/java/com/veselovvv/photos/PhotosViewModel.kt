package com.veselovvv.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotosViewModel : ViewModel() {
    val photoLiveData: LiveData<List<Photo>>

    init {
        photoLiveData = FlickrFetchr().fetchImages()
    }
}