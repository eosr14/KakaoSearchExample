package com.eosr14.example.kakao.ui.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.eosr14.example.kakao.R
import com.eosr14.example.kakao.common.base.BaseActivity
import com.eosr14.example.kakao.common.event.*
import com.eosr14.example.kakao.common.inject
import com.eosr14.example.kakao.databinding.ActivityDetailBinding
import com.eosr14.example.kakao.model.Document

class DetailActivity : BaseActivity() {

    private val viewModel: DetailViewModel by lazy {
        DetailViewModel().inject(this)
    }

    private var binding: ActivityDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityDetailBinding>(
            this,
            R.layout.activity_detail
        ).apply {
            viewModel = this@DetailActivity.viewModel
            lifecycleOwner = this@DetailActivity
            executePendingBindings()
            binding = this
            setIntentData()
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

    private fun setIntentData() {
        intent?.extras?.let { bundle ->
            bundle.get(KEY_DOCUMENT)?.let {
                (it as? Document)?.let { document ->
                    viewModel.setDocument(document)
                }
            }

            bundle.get(KEY_POSITION)?.let {
                (it as? Int)?.let { position ->
                    viewModel.setPosition(position)
                }
            }
        }
    }

    private fun eventObserve() {
        addDisposable(
            RxEventBus.getSingleEventType(EventBusInterface::class.java)
                .compose(bindToLifecycle())
                .subscribe { event ->
                    when (event) {
                        is OnClickBackButtonEvent -> finish()
                        is OnClickUrlEvent -> {
                            event.url?.let {
                                startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                )

                                setResult(RESULT_URL_CLICK, Intent().apply {
                                    viewModel.position.value?.let { position -> putExtra(KEY_POSITION, position) }
                                })

                            }

                        }
                    }
                })
    }

    companion object {
        private const val REQUEST_CODE_DETAIL = 10000
        const val RESULT_URL_CLICK = 10001

        const val KEY_DOCUMENT = "key_document"
        const val KEY_POSITION = "key_position"

        fun startActivityForResult(activity: Activity, document: Document, position: Int) {
            activity.startActivityForResult(Intent(activity, DetailActivity::class.java).apply {
                putExtra(KEY_DOCUMENT, document)
                putExtra(KEY_POSITION, position)
            }, REQUEST_CODE_DETAIL)
        }
    }

}