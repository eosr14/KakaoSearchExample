package com.eosr14.example.kakao.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchModel(
    @SerializedName("documents") @Expose val documents: ArrayList<Document>,
    @SerializedName("meta") @Expose val meta: Meta
)