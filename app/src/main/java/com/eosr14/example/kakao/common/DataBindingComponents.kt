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
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder


object DataBindingComponents {

    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("replaceAll")
    @JvmStatic
    fun setReplaceItems(view: RecyclerView, items: List<Any>?) {
        (view.adapter as? BaseRecyclerViewAdapter<Any, *>)?.run {
            replaceAll(items)
            notifyDataSetChanged()
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
//                val isEnd = viewModel.isEnd.value ?: false
//                val page = viewModel.page.value ?: 1
//
//                if (!recyclerView.canScrollVertically(1) && page < MAX_PAGE_COUNT && !isEnd) {
////                    viewModel.get
////                    viewModel.requestSearchImage(viewModel.searchText.value ?: "", true)
//                }
            }
        })
    }

    @JvmStatic
    @BindingAdapter("dateTime")
    fun setDataTime(view: TextView, dateTime: String?) {
        dateTime?.let {
            when {
                dateTime.convertIsoDate().isYesterday() -> view.text = view.context.getString(R.string.yesterday)
                dateTime.convertIsoDate().isToday() -> view.text = view.context.getString(R.string.today)
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
