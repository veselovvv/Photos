package com.veselovvv.photos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.veselovvv.photos.api.FlickrApi
import com.veselovvv.photos.api.FlickrResponse
import com.veselovvv.photos.api.PhotoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FlickrFetchr {
    private val flickrApi: FlickrApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchImages(): LiveData<List<Photo>> {
        val responseLiveData: MutableLiveData<List<Photo>> = MutableLiveData()
        val flickrRequest = flickrApi.fetchImages()

        // Выполнение веб-запроса, содержащегося в объекте Call:
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) = Unit
            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var photos: List<Photo> = photoResponse?.photos ?: mutableListOf()

                photos = photos.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value = photos
            }
        })
        return responseLiveData
    }

    @WorkerThread
    fun fetchImage(url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        return response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
    }
}