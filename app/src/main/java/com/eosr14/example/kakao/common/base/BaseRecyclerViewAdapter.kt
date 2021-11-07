package com.eosr14.example.kakao.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eosr14.example.kakao.databinding.LayoutItemsTermBinding
import com.eosr14.example.kakao.model.Terms
import io.reactivex.functions.BiConsumer

class BaseRecyclerViewAdapter<ITEM : Any, B : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
    private val bindingVariableId: Int? = null,
    private val onClickListener: BiConsumer<ITEM, Int>,
) : RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder<B>>() {

    private var items = ArrayList<ITEM>()

    fun setItems(items: List<ITEM>?) {
        items?.let {
            this.items = items as ArrayList<ITEM>
            notifyDataSetChanged()
        }
    }

    fun pushItems(items: List<ITEM>?) {
        items?.let {
            this.items.addAll(items)
            notifyItemInserted(this.items.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false).apply {
            setOnClickListener {
                onClickListener.accept(items[it.tag as Int], it.tag as Int)
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

//    data class Toggle(
//        val title: String
////    var isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
//    ) {
//        var isChecked: MutableLiveData<Boolean> = MutableLiveData(false)
//    }

    class ViewHolder<B : ViewDataBinding>(
        private val bindingVariableId: Int?,
        view: View
    ) : RecyclerView.ViewHolder(view) {
        private var lifecycleOwner: LifecycleOwner? = null
        private val binding: B? = DataBindingUtil.bind(itemView)

        init {
            itemView.doOnAttach {
                lifecycleOwner = itemView.findViewTreeLifecycleOwner()
            }
            itemView.doOnDetach {
                lifecycleOwner = null
            }
        }

        fun onBindViewHolder(item: Any?, position: Int) {
            itemView.tag = position
            try {
                bindingVariableId?.let {
                    binding?.setVariable(it, item)
                }
                binding?.lifecycleOwner = this@ViewHolder.lifecycleOwner
//                binding?.lifecycleOwner = itemView.findViewTreeLifecycleOwner()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun View.findViewTreeLifecycleOwner(): LifecycleOwner? =
            ViewTreeLifecycleOwner.get(this)
    }

    fun updateList(items: List<Terms>?) {
        items?.let {
            val diffCallback = DiffDefault(this.items as List<Terms>, items)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.items.run {
                clear()
                addAll(items as List<ITEM>)
                diffResult.dispatchUpdatesTo(this@BaseRecyclerViewAdapter)
            }
        }
    }

    private class DiffDefault(
        private val oldItems: List<Terms>,
        private val newItems: List<Terms>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]

            return oldItem == newItem
        }
    }


}