package com.eosr14.example.kakao.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.functions.Consumer

class BaseRecyclerViewAdapter<ITEM : Any, B : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
    private val bindingVariableId: Int? = null,
    private val onClickListener: Consumer<ITEM>,
) : RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder<B>>() {

    private var items = ArrayList<ITEM>()

    fun replaceAll(items: List<ITEM>?) {
        items?.let {
            this.items.clear()
            this.items.addAll(it)
        }
    }

    fun bindList(items: List<ITEM>?) {
        items?.let {
            this.items = items as ArrayList<ITEM>
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false).apply {
            setOnClickListener {
                onClickListener.accept(items[it.tag as Int])
            }
        }
        return ViewHolder(bindingVariableId, view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        holder.onBindViewHolder(items[position], position)
    }

    class ViewHolder<B : ViewDataBinding>(
        private val bindingVariableId: Int?,
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val binding: B? = DataBindingUtil.bind(itemView)

        fun onBindViewHolder(item: Any?, position: Int) {
            itemView.tag = position
            try {
                bindingVariableId?.let {
                    binding?.setVariable(it, item)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}