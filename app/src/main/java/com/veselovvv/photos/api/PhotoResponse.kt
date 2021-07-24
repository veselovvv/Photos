package com.veselovvv.photos.api

import com.google.gson.annotations.SerializedName
import com.veselovvv.photos.Photo

class PhotoResponse {
    @SerializedName("photo")
    lateinit var photos: List<Photo>
}