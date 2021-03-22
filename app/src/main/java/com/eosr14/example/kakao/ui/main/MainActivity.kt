package com.eosr14.example.kakao.ui.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.LinearLayoutManager
import com.eosr14.example.kakao.R
import com.eosr14.example.kakao.common.ItemsDialog
import com.eosr14.example.kakao.common.base.BaseActivity
import com.eosr14.example.kakao.common.base.BaseRecyclerViewAdapter
import com.eosr14.example.kakao.common.event.*
import com.eosr14.example.kakao.common.inject
import com.eosr14.example.kakao.databinding.ActivityMainBinding
import com.eosr14.example.kakao.databinding.LayoutItemsSearchBinding
import com.eosr14.example.kakao.model.DialogItem
import com.eosr14.example.kakao.model.Document
import com.eosr14.example.kakao.network.RetrofitClient
import com.eosr14.example.kakao.network.services.KaKaoService


class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by lazy {
        MainViewModel(
            this@MainActivity,
            RetrofitClient.getInstance()?.create(KaKaoService::class.java)
        ).inject(this)
    }

    private var binding: ActivityMainBinding? = null

    private var documentAdapter = BaseRecyclerViewAdapter<Document, LayoutItemsSearchBinding>(
        layoutResId = R.layout.layout_items_search,
        bindingVariableId = BR.document,
        onClickListener = {
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        ).apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
            executePendingBindings()
            binding = this
            bindView()
        }
    }

    override fun onResume() {
        super.onResume()
        eventObserve()
    }

    override fun onPause() {
        super.onPause()
        clearDisposable()
    }

    private fun bindView() {
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            viewModel.filterList
        )

        binding?.spMainFilter?.apply {
            adapter = spinnerArrayAdapter.apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setCurrentFilter(viewModel.filterList[position])
                }
            }
        }

        binding?.rvMainSearch?.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = documentAdapter
        }
    }

    private fun showSortDialog() {
        val listItems = ArrayList<DialogItem>()
        for ((index, name) in viewModel.sortList.withIndex()) {
            val isChecked = when {
                (name == viewModel.currentSort.value.toString()) -> true
                else -> false
            }
            listItems.add(DialogItem(index, name, isChecked = isChecked))
        }

        ItemsDialog(this, ItemsDialog.Builder(
            titleStr = getString(R.string.sort),
            items = listItems,
            selectName = viewModel.currentSort.value.toString()
        ) { selectName ->
            viewModel.setCurrentSort(selectName)
        }).show()
    }

    private fun eventObserve() {
        addDisposable(
            RxEventBus.getSingleEventType(EventBusInterface::class.java)
                .compose(bindToLifecycle())
                .subscribe { event ->
                    when (event) {
                        is ToastEvent -> Toast.makeText(
                            this@MainActivity,
                            event.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        is OnClickSearchEvent -> {
                            binding?.edittextMainSearch?.let {
                                if (it.text.toString().isEmpty()) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.main_search_term_empty),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@subscribe
                                }

                                when (viewModel.currentFilter.value) {
                                    SearchFilter.ALL.type -> viewModel.getAllData(
                                        query = it.text.toString(),
                                        page = 1
                                    )

                                    SearchFilter.BLOG.type -> viewModel.getBlog(
                                        query = it.text.toString(),
                                        page = 1
                                    )

                                    SearchFilter.CAFE.type -> viewModel.getCafe(
                                        query = it.text.toString(),
                                        page = 1
                                    )
                                }
                            }
                        }

                        is OnClickSortDialogEvent -> showSortDialog()

                    }
                })
    }

}