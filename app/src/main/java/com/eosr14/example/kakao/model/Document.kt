package com.eosr14.example.kakao.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("cafename") @Expose val cafeName: String?,
    @SerializedName("blogname") @Expose val blogName: String?,
    @SerializedName("contents") @Expose val contents: String,
    @SerializedName("datetime") @Expose val datetime: String,
    @SerializedName("thumbnail") @Expose val thumbnail: String,
    @SerializedName("title") @Expose val title: String,
    @SerializedName("url") @Expose val url: String
)