package com.eosr14.example.kakao.model

import androidx.lifecycle.MutableLiveData

data class Terms(
    val index: Int,
    val termsTitle: String,
    val isRequired: Boolean,
    val content: String,
    var isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
)