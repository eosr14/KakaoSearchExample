package com.eosr14.example.kakao.ui.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eosr14.example.kakao.common.base.BaseViewModel
import com.eosr14.example.kakao.model.Terms
import okhttp3.internal.filterList
import java.util.*

class TermsViewModel : BaseViewModel() {

    private val _terms = MutableLiveData<ArrayList<Terms>>()
    val terms: LiveData<ArrayList<Terms>> get() = _terms

    private val requiredTermsSize = _terms.value?.filter { it.isRequired }?.size ?: 0
    private val isFullAgreement = MutableLiveData(false)
    private val isEnableButton = MutableLiveData(false)

    init {
        _terms.value = makeItems()
    }

    private fun makeItems(): ArrayList<Terms>? {
        val list = ArrayList<Terms>().apply {
            add(Terms(1, "이용약관1", true, "이용약관1"))
            add(Terms(2, "이용약관2", true, "이용약관2"))
            add(Terms(3, "이용약관3", true, "이용약관3"))
            add(Terms(4, "이용약관4", false, "이용약관4"))
        }
        return list
    }

    fun onClickTermsItem() {
        _terms.value?.let { list ->
            isFullAgreement.value = list.filter { it.isChecked.value ?: false }.size == list.size

            isEnableButton.value =
                list.filter { it.isChecked.value ?: false && it.isRequired ?: false }.size == requiredTermsSize
        }

    }

    fun onClickFullAgreement() {
        _terms.value?.let {
            it.forEach { terms ->
                terms.isChecked = isFullAgreement
            }

            isEnableButton.value = isFullAgreement.value ?: false

        }
    }

}