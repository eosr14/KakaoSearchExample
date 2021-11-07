package com.eosr14.example.kakao.ui.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eosr14.example.kakao.common.base.BaseViewModel
import com.eosr14.example.kakao.common.extension.notifyObserver
import com.eosr14.example.kakao.model.Terms
import java.util.*

class TermsViewModel : BaseViewModel() {

    private val _terms = MutableLiveData<ArrayList<Terms>>()
    val terms: LiveData<ArrayList<Terms>> get() = _terms

//    private val requiredTermsSize = _terms.value?.filter { it.isRequired }?.size ?: 0
    val isFullAgreement = MutableLiveData(false)
    val isEnableButton = MutableLiveData(false)

    init {
        _terms.value = makeItems()
    }

    private fun makeItems(): ArrayList<Terms> {
        val list = ArrayList<Terms>().apply {
            add(Terms(1, "이용약관1", true))
            add(Terms(2, "이용약관2", true))
            add(Terms(3, "이용약관3", true))
            add(Terms(4, "이용약관4", false))
        }
        return list
    }

    fun onClickTermsItem(item: Terms, position: Int) {
        item.isChecked.value = item.isChecked.value?.not()
        _terms.notifyObserver()

        val requiredTermsSize = _terms.value?.filter { it.isRequired }?.size ?: 0

        _terms.value?.let { list ->
            isFullAgreement.value = list.filter { it.isChecked.value == true }.size == list.size

            android.util.Log.d("eosr14", "requiredTermsSize size = ${list.filter { it.isChecked.value == true && it.isRequired }.size} $requiredTermsSize")
            isEnableButton.value =
                list.filter { it.isChecked.value == true && it.isRequired }.size == requiredTermsSize
        }
    }

    fun onClickFullAgreement() {
        isFullAgreement.value = isFullAgreement.value?.not()

        _terms.value?.let {
            it.forEach { terms ->
                terms.isChecked.value = isFullAgreement.value
            }
        }
        _terms.notifyObserver()

        isEnableButton.value = isFullAgreement.value
    }

}