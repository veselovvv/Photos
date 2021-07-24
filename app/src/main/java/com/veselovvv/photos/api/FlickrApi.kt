package com.veselovvv.photos.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

private const val KEY = "YourKey"

interface FlickrApi {

    // Запрос на получение недавних интересных фотографий:
    @GET("services/rest/?method=flickr.interestingness.getList&api_key=" + KEY +
            "&format=json&nojsoncallback=1&extras=url_s")
    fun fetchImages(): Call<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>
}