package com.eosr14.example.kakao.model

import androidx.lifecycle.MutableLiveData

data class Toggle(
    val title: String
//    var isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
) {
    var isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
}