package com.eosr14.example.kakao.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eosr14.example.kakao.R
import com.eosr14.example.kakao.common.SchedulerProvider
import com.eosr14.example.kakao.common.base.BaseViewModel
import com.eosr14.example.kakao.common.event.OnClickSearchEvent
import com.eosr14.example.kakao.common.event.OnClickSortDialogEvent
import com.eosr14.example.kakao.common.event.RxEventBus
import com.eosr14.example.kakao.common.event.ToastEvent
import com.eosr14.example.kakao.common.extension.notifyObserver
import com.eosr14.example.kakao.model.Document
import com.eosr14.example.kakao.model.SearchModel
import com.eosr14.example.kakao.network.services.KaKaoService
import io.reactivex.observers.DisposableSingleObserver


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
    private val service: KaKaoService?,
    private val schedulerProvider: SchedulerProvider,
) : BaseViewModel() {

    private val _currentFilter = MutableLiveData(SearchFilter.ALL.type)
    val currentFilter: LiveData<String> get() = _currentFilter

    private val _currentSort = MutableLiveData(SearchSort.TITLE.sort)
    val currentSort: LiveData<String> get() = _currentSort

    private val _searchList = MutableLiveData<ArrayList<Document>>(arrayListOf())
    val searchList: LiveData<ArrayList<Document>> get() = _searchList

    private val _page = MutableLiveData(1)
    val page: LiveData<Int> get() = _page

    private val _isEnd = MutableLiveData(false)
    val isEnd: LiveData<Boolean> get() = _isEnd

    private val _totalCount = MutableLiveData(0)
    val totalCount: LiveData<Int> get() = _totalCount

    private val _query = MutableLiveData("")
    val query: LiveData<String> get() = _query

    private val _isFirst = MutableLiveData(false)
    val isFirst: LiveData<Boolean> get() = _isFirst

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
        size: Int = DEFAULT_PAGE_SIZE,
        isFirst: Boolean
    ) {
        _isFirst.value = isFirst
        _searchList.value = arrayListOf()

        when (isFirst) {
            true -> _page.value = 1
            false -> _page.value = _page.value?.plus(1)
        }

        service?.let { service ->
            _page.value?.let { page ->
                service.getBlog(query, sort, page, size)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(object : DisposableSingleObserver<SearchModel>() {
                        override fun onSuccess(t: SearchModel) {
                            _query.value = query
                            _totalCount.value = t.meta.totalCount

                            when (isFirst) {
                                true -> _isEnd.value = false
                                false -> _isEnd.value = t.meta.isEnd
                            }

                            _searchList.value?.apply {
                                addAll(
                                    when (_currentSort.value) {
                                        SearchSort.DATE_TIME.sort -> t.documents.sortedByDescending { it.datetime }
                                        else -> t.documents.sortedBy { it.title }
                                    }
                                )
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
    }

    fun getCafe(
        query: String,
        sort: String = TYPE_FILTER_ACCURACY,
        size: Int = DEFAULT_PAGE_SIZE,
        isFirst: Boolean
    ) {
        _isFirst.value = isFirst
        _searchList.value = arrayListOf()

        when (isFirst) {
            true -> _page.value = 1
            false -> _page.value = _page.value?.plus(1)
        }

        service?.let { service ->
            _page.value?.let { page ->
                service.getCafe(query, sort, page, size)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(object : DisposableSingleObserver<SearchModel>() {
                        override fun onSuccess(t: SearchModel) {
                            _query.value = query
                            _totalCount.value = t.meta.totalCount

                            when (isFirst) {
                                true -> _isEnd.value = false
                                false -> _isEnd.value = t.meta.isEnd
                            }

                            _searchList.value?.apply {
                                addAll(
                                    when (_currentSort.value) {
                                        SearchSort.DATE_TIME.sort -> t.documents.sortedByDescending { it.datetime }
                                        else -> t.documents.sortedBy { it.title }
                                    }
                                )
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
    }

    fun getAllData(
        query: String,
        sort: String = TYPE_FILTER_ACCURACY,
        size: Int = DEFAULT_PAGE_SIZE,
        isFirst: Boolean
    ) {
        _isFirst.value = isFirst
        _searchList.value = arrayListOf()

        when (isFirst) {
            true -> _page.value = 1
            false -> _page.value = _page.value?.plus(1)
        }

        service?.let { service ->
            _page.value?.let { page ->
                service.getBlog(query, sort, page, size)
                    .flatMap {
                        _searchList.value?.apply {
                            addAll(
                                when (_currentSort.value) {
                                    SearchSort.DATE_TIME.sort -> it.documents.sortedByDescending { it.datetime }
                                    else -> it.documents.sortedBy { it.title }
                                }
                            )
                        }

                        return@flatMap service.getCafe(query, sort, page, size)
                    }
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(object : DisposableSingleObserver<SearchModel>() {
                        override fun onSuccess(t: SearchModel) {
                            _query.value = query
                            _totalCount.value = t.meta.totalCount

                            when (isFirst) {
                                true -> _isEnd.value = false
                                false -> _isEnd.value = t.meta.isEnd
                            }

                            _searchList.value?.addAll(
                                when (_currentSort.value) {
                                    SearchSort.DATE_TIME.sort -> t.documents.sortedByDescending { it.datetime }
                                    else -> t.documents.sortedBy { it.title }
                                }
                            )

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
    }

    fun setListWithDim(position: Int) {
        _searchList.value?.forEachIndexed { index, document ->
            if (position == index) {
                document.isDim = true
            }
        }
        _searchList.notifyObserver()
    }

    fun onClickSearch() = RxEventBus.sendEvent(OnClickSearchEvent())

    fun onClickSortDialog() = RxEventBus.sendEvent(OnClickSortDialogEvent())

    companion object {
        private const val TYPE_FILTER_ACCURACY = "accuracy"
        const val DEFAULT_PAGE_SIZE = 25
    }

}