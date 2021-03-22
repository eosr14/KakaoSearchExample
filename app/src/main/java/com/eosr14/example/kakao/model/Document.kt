package com.eosr14.example.kakao.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Document(
    @SerializedName("cafename") @Expose val cafeName: String?,
    @SerializedName("blogname") @Expose val blogName: String?,
    @SerializedName("contents") @Expose val contents: String,
    @SerializedName("datetime") @Expose val datetime: String,
    @SerializedName("thumbnail") @Expose val thumbnail: String,
    @SerializedName("title") @Expose val title: String,
    @SerializedName("url") @Expose val url: String,
    var isDim: Boolean = false
) : Parcelable