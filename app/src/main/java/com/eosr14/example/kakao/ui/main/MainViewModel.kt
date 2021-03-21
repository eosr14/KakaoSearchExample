package com.eosr14.example.kakao.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eosr14.example.kakao.R
import com.eosr14.example.kakao.common.base.BaseViewModel
import com.eosr14.example.kakao.common.event.OnClickSearchEvent
import com.eosr14.example.kakao.common.event.OnClickSortDialogEvent
import com.eosr14.example.kakao.common.event.RxEventBus
import com.eosr14.example.kakao.common.event.ToastEvent
import com.eosr14.example.kakao.common.extension.notifyObserver
import com.eosr14.example.kakao.model.Document
import com.eosr14.example.kakao.model.SearchModel
import com.eosr14.example.kakao.network.services.KaKaoService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


enum class SearchFilter(val type: String) {
    ALL("All"),
    BLOG("Blog"),
    CAFE("Cafe")
}

enum class SearchSort(val sort: String) {
    TITLE("Title"),
    DATE_TIME("DateTime")
}

class MainViewModel(
    private val context: Context?,
    private val service: KaKaoService?
) : BaseViewModel() {

    private val _currentFilter = MutableLiveData(SearchFilter.ALL.type)
    val currentFilter: LiveData<String> = _currentFilter

    private val _currentSort = MutableLiveData(SearchSort.TITLE.sort)
    val currentSort: LiveData<String> = _currentSort

    private val _searchList = MutableLiveData<ArrayList<Document>>(arrayListOf())
    val searchList: LiveData<ArrayList<Document>> = _searchList

    var sortList = listOf<String>()
    var filterList = listOf<String>()

    init {
        sortList = SearchSort.values().map {
            it.sort
        }

        filterList = SearchFilter.values().map {
            it.type
        }
    }

    fun setCurrentSort(sort: String) {
        _currentSort.value = sort
    }

    fun setCurrentFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun getBlog(
        query: String,
        sort: String = TYPE_FILTER_ACCURACY,
        page: Int,
        size: Int = DEFAULT_PAGE_SIZE
    ) {
        service?.let { service ->
            service.getBlog(query, sort, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<SearchModel>() {
                    override fun onSuccess(t: SearchModel) {
                        _searchList.value?.apply {
                            clear()
                            addAll(t.documents)
                        }
                        _searchList.notifyObserver()
                    }

                    override fun onError(e: Throwable) {
                        context?.let {
                            RxEventBus.sendEvent(ToastEvent(it.getString(R.string.api_failed_message)))
                        }
                    }
                })
        }
    }

    fun getCafe(
        query: String,
        sort: String = TYPE_FILTER_ACCURACY,
        page: Int,
        size: Int = DEFAULT_PAGE_SIZE
    ) {
        service?.let { service ->
            service.getCafe(query, sort, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<SearchModel>() {
                    override fun onSuccess(t: SearchModel) {
                        _searchList.value?.apply {
                            clear()
                            addAll(t.documents)
                        }
                        _searchList.notifyObserver()
                    }

                    override fun onError(e: Throwable) {
                        context?.let {
                            RxEventBus.sendEvent(ToastEvent(it.getString(R.string.api_failed_message)))
                        }
                    }
                })
        }
    }

    fun getAllData(
        query: String,
        sort: String = TYPE_FILTER_ACCURACY,
        page: Int,
        size: Int = DEFAULT_PAGE_SIZE
    ) {
        service?.let { service ->
            service.getBlog(query, sort, page, size)
                .flatMap {
                    _searchList.value?.apply {
                        clear()
                        addAll(it.documents)
                    }

                    return@flatMap service.getCafe(query, sort, page, size)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<SearchModel>() {
                    override fun onSuccess(t: SearchModel) {
                        _searchList.value?.addAll(t.documents)
                        _searchList.notifyObserver()
                    }

                    override fun onError(e: Throwable) {
                        context?.let {
                            RxEventBus.sendEvent(ToastEvent(it.getString(R.string.api_failed_message)))
                        }
                    }
                })
        }
    }


    fun onClickSearch() = RxEventBus.sendEvent(OnClickSearchEvent())

    fun onClickSortDialog() = RxEventBus.sendEvent(OnClickSortDialogEvent())

    companion object {
        private const val TYPE_FILTER_ACCURACY = "accuracy"
        private const val DEFAULT_PAGE_SIZE = 25
    }

}