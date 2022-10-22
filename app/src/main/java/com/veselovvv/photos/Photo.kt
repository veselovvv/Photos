package com.veselovvv.photos

import com.google.gson.annotations.SerializedName

data class Photo(
    private var title: String = "",
    private var id: String = "",
    // Показывает Gson к какому JSON-полю относится свойство:
    @SerializedName("url_s")
    private var url: String = ""
) {
    fun getUrl() = url
}