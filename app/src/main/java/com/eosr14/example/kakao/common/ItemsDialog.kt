package com.eosr14.example.kakao.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.LinearLayoutManager
import com.eosr14.example.kakao.R
import com.eosr14.example.kakao.common.base.BaseRecyclerViewAdapter
import com.eosr14.example.kakao.databinding.LayoutItemsDialogBinding
import com.eosr14.example.kakao.databinding.LayoutItemsDialogRowBinding
import com.eosr14.example.kakao.model.DialogItem
import io.reactivex.functions.Consumer

class ItemsDialog(context: Context) : Dialog(context) {

    private var titleStr: String = ""
    private var items: List<DialogItem>? = null
    private var selectName: String = ""
    private var onItemCallBack: Consumer<String>? = null
    private var binding: LayoutItemsDialogBinding? = null

    constructor(context: Context, builder: Builder) : this(context) {
        this.titleStr = builder.titleStr
        this.items = builder.items
        this.selectName = builder.selectName
        this.onItemCallBack = builder.onItemCallBack
    }

    init {
        this.setCanceledOnTouchOutside(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.inflate<LayoutItemsDialogBinding>(
            LayoutInflater.from(context),
            R.layout.layout_items_dialog,
            null,
            false
        ).apply {
            setContentView(this.root)
        }

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)
    }

    override fun show() {
        super.show()
        setLayout()
    }

    private fun setLayout() {
        binding?.tvItemsTitle?.text = titleStr

        binding?.rvItems?.apply {
            layoutManager = LinearLayoutManager(this.context)

            items?.let {
                adapter = dialogAdapter
                dialogAdapter.run { bindList(it) }
            }
        }

        binding?.btnConfirm?.setOnClickListener {
            dismiss()
            onItemCallBack?.accept(selectName)
        }

        binding?.btnCancel?.setOnClickListener { dismiss() }
    }

    private fun updateItems() {
        items?.forEach {
            it.isChecked = it.name == selectName
        }

        dialogAdapter.apply {
            bindList(items)
            notifyDataSetChanged()
        }
    }

    private var dialogAdapter = BaseRecyclerViewAdapter<DialogItem, LayoutItemsDialogRowBinding>(
        layoutResId = R.layout.layout_items_dialog_row,
        bindingVariableId = BR.dialogItem,
        onClickListener = {
            selectName = it.name
            updateItems()
        }
    )

    data class Builder(
        var titleStr: String = "",
        var items: ArrayList<DialogItem>? = null,
        var selectName: String = "",
        var onItemCallBack: Consumer<String>? = null
    ) {
        fun titleStr(titleStr: String) = apply {
            this.titleStr = titleStr
        }

        fun items(items: ArrayList<DialogItem>) = apply {
            this.items = items
        }

        fun selectName(name: String) = apply {
            this.selectName = name
        }

        fun onItemCallBack(callBack: Consumer<String>) = apply {
            this.onItemCallBack = callBack
        }
    }

}