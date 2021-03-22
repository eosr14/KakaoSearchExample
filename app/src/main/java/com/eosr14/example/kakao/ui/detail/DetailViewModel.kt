package com.eosr14.example.kakao.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eosr14.example.kakao.common.base.BaseViewModel
import com.eosr14.example.kakao.common.event.OnClickBackButtonEvent
import com.eosr14.example.kakao.common.event.OnClickUrlEvent
import com.eosr14.example.kakao.common.event.RxEventBus
import com.eosr14.example.kakao.model.Document

class DetailViewModel : BaseViewModel() {

    private val _document = MutableLiveData<Document>()
    val document: LiveData<Document> = _document

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int> = _position

    fun setDocument(document: Document) {
        _document.value = document
    }

    fun setPosition(position: Int) {
        _position.value = position
    }

    fun onClickBackButton() = RxEventBus.sendEvent(OnClickBackButtonEvent())

    fun onClickUrl(url: String?) = RxEventBus.sendEvent(OnClickUrlEvent(url))

}