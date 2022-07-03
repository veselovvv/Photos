package com.veselovvv.photos

import com.google.gson.annotations.SerializedName

data class Photo(
    var title: String = "",
    var id: String = "",
    // Показывает Gson к какому JSON-полю относится свойство:
    @SerializedName("url_s")
    var url: String = ""
)