package com.eosr14.example.kakao.common

import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eosr14.example.kakao.R
import com.eosr14.example.kakao.common.base.BaseRecyclerViewAdapter
import com.eosr14.example.kakao.common.extension.convertDisplayDate
import com.eosr14.example.kakao.common.extension.convertIsoDate
import com.eosr14.example.kakao.common.extension.isToday
import com.eosr14.example.kakao.common.extension.isYesterday
import com.eosr14.example.kakao.ui.main.MainViewModel
import com.eosr14.example.kakao.ui.main.MainViewModel.Companion.DEFAULT_PAGE_SIZE
import com.eosr14.example.kakao.ui.main.SearchFilter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlin.math.ceil


object DataBindingComponents {

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter("searchItems", "isFirst")
    fun setSearchItems(
        recyclerView: RecyclerView,
        items: List<Any>?,
        isFirst: Boolean
    ) {
        (recyclerView.adapter as? BaseRecyclerViewAdapter<Any, *>)?.run {
            when (isFirst) {
                true -> setItems(items)
                false -> pushItems(items)
            }
            recyclerView.scheduleLayoutAnimation()
        }
    }

    @JvmStatic
    @BindingAdapter("textToHtml")
    fun setTextToHtml(view: TextView, text: String?) {
        text?.let {
            view.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(text)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("scrollBottomListener")
    fun setScrollBottomListener(recyclerView: RecyclerView, viewModel: MainViewModel) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val page = viewModel.page.value ?: 1
                val totalCount = viewModel.totalCount.value ?: 0
                val filterType = viewModel.currentFilter.value ?: SearchFilter.ALL.type
                val isEnd = checkViewMoreIsEnd(page, totalCount, filterType)
                val query = viewModel.query.value ?: ""

                if (!recyclerView.canScrollVertically(1) && page < totalCount && !isEnd) {
                    when (filterType) {
                        SearchFilter.ALL.type -> viewModel.getAllData(query = query, isFirst = false)
                        SearchFilter.BLOG.type -> viewModel.getBlog(query = query, isFirst = false)
                        SearchFilter.CAFE.type -> viewModel.getCafe(query = query, isFirst = false)
                    }
                }
            }
        })
    }

    private fun checkViewMoreIsEnd(page: Int, totalCount: Int, filterType: String): Boolean {
        val pageSize = when (filterType) {
            SearchFilter.ALL.type -> DEFAULT_PAGE_SIZE * 2
            else -> DEFAULT_PAGE_SIZE
        }
        return page >= ceil(totalCount.toDouble() / pageSize.toDouble())
    }


    @JvmStatic
    @BindingAdapter("dateTime")
    fun setDataTime(view: TextView, dateTime: String?) {
        dateTime?.let {
            when {
                dateTime.convertIsoDate().isYesterday() -> view.text =
                    view.context.getString(R.string.yesterday)
                dateTime.convertIsoDate().isToday() -> view.text =
                    view.context.getString(R.string.today)
                else -> view.text = dateTime.convertDisplayDate()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("urlToImage")
    fun setUrlToImage(view: SimpleDraweeView, url: String?) {
        url?.let {
            val imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setRotationOptions(RotationOptions.autoRotate())
                .setProgressiveRenderingEnabled(true)
                .build()

            view.controller = Fresco.newDraweeControllerBuilder().run {
                this.oldController = view.controller
                this.imageRequest = imageRequest
                this.autoPlayAnimations = true
                this.controllerListener = object : BaseControllerListener<ImageInfo>() {
                    override fun onFinalImageSet(
                        id: String?,
                        imageInfo: ImageInfo?,
                        animatable: Animatable?
                    ) {
                        super.onFinalImageSet(id, imageInfo, animatable)
                        imageInfo?.let { info ->
                            view.aspectRatio = info.width.toFloat() / info.height
                        }
                    }
                }
                this.build()
            }
        }
    }

}
