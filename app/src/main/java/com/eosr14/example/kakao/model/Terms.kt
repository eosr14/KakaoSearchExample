package com.eosr14.example.kakao.model

import androidx.lifecycle.MutableLiveData

data class Terms(
    val index: Int,
    val termsTitle: String,
    val isRequired: Boolean,
//    var isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
) {
    var isChecked = MutableLiveData(false)
}