package com.eosr14.example.kakao.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("is_end") @Expose val isEnd: Boolean,
    @SerializedName("pageable_count") @Expose val pageableCount: Int,
    @SerializedName("total_count") @Expose val totalCount: Int
)