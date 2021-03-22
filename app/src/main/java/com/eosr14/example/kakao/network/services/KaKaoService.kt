package com.eosr14.example.kakao.network.services

import com.eosr14.example.kakao.model.SearchModel
import com.eosr14.example.kakao.network.RetrofitClient
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KaKaoService {

    @Headers("Authorization: KakaoAK ${RetrofitClient.KAKAO_API_KEY}")
    @GET("/v2/search/cafe")
    fun getCafe(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<SearchModel>

    @Headers("Authorization: KakaoAK ${RetrofitClient.KAKAO_API_KEY}")
    @GET("/v2/search/blog")
    fun getBlog(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<SearchModel>

}