package com.eosr14.example.kakao.ui.terms

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.LinearLayoutManager
import com.eosr14.example.kakao.R
import com.eosr14.example.kakao.common.base.BaseActivity
import com.eosr14.example.kakao.common.base.BaseRecyclerViewAdapter
import com.eosr14.example.kakao.common.inject
import com.eosr14.example.kakao.databinding.ActivityTermsBinding
import com.eosr14.example.kakao.databinding.LayoutItemsTermBinding
import com.eosr14.example.kakao.model.Terms

class TermsActivity : BaseActivity() {

    private val viewModel: TermsViewModel by lazy {
        TermsViewModel().inject(this)
    }

    private var binding: ActivityTermsBinding? = null

    private var adapter = BaseRecyclerViewAdapter<Terms, LayoutItemsTermBinding>(
        layoutResId = R.layout.layout_items_term,
        bindingVariableId = BR.terms,
        onClickListener = { terms, position ->
            viewModel.onClickTermsItem(terms, position)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityTermsBinding>(
            this,
            R.layout.activity_terms
        ).apply {
            viewModel = this@TermsActivity.viewModel
            lifecycleOwner = this@TermsActivity
            executePendingBindings()
            binding = this
            bindView()
        }
    }

    private fun bindView() {
        binding?.rvTerms?.run {
            layoutManager = LinearLayoutManager(this@TermsActivity)
            adapter = this@TermsActivity.adapter
        }

        viewModel.terms.observe(this) {
            adapter.setItems(it)
        }
    }

}