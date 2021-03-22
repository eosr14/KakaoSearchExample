package com.eosr14.example.kakao.common

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RequestBodyUtil {

    companion object {
        fun jsonBody(body: String): RequestBody {
            return body.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        }

        fun textHtmlBody(body: String): RequestBody {
            return body.toRequestBody("text/html; charset=utf-8".toMediaTypeOrNull())
        }
    }

}