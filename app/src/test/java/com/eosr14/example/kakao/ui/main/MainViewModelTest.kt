@file:Suppress("NonAsciiCharacters")

package com.eosr14.example.kakao.ui.main

import android.content.Context
import android.text.TextUtils
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eosr14.example.kakao.KaKaoSearchApplication
import com.eosr14.example.kakao.common.SchedulerProvider
import com.eosr14.example.kakao.common.TestScheduler
import com.eosr14.example.kakao.model.SearchModel
import com.eosr14.example.kakao.network.services.KaKaoService
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(TextUtils::class, KaKaoSearchApplication::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val context: Context = Mockito.mock(Context::class.java)
    private val service: KaKaoService = Mockito.mock(KaKaoService::class.java)
    private val schedulerProvider: SchedulerProvider = TestScheduler()
    private val viewModel: MainViewModel by lazy {
        MainViewModel(context, service, schedulerProvider)
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(context.applicationContext).thenReturn(context)

        PowerMockito.mockStatic(TextUtils::class.java)
        PowerMockito.mockStatic(KaKaoSearchApplication::class.java)

        RxJavaPlugins.setIoSchedulerHandler { schedulerProvider.io() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { schedulerProvider.ui() }
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun `결과값이 비어 있을 시`() {
        // given
        Mockito.`when`(service.getCafe("테스트", "accuracy", 1, 25))
            .thenReturn(Single.just(emptySearchModel()))

        // when
        viewModel.getCafe("테스트", "accuracy", 25, true)

        // then
        println("${viewModel.searchList.value}")
        Assert.assertEquals(
            "결과값이 비어 있을 시",
            true,
            viewModel.searchList.value?.isEmpty()
        )
    }

    @Test
    fun `정상적으로 데이터가 들어왔을 시`() {
        // given
        Mockito.`when`(service.getBlog("테스트", "accuracy", 1, 25))
            .thenReturn(Single.just(successSearchModel()))

        // when
        viewModel.getBlog("테스트", "accuracy", 25, true)

        // then
        println("${viewModel.searchList.value}")
        Assert.assertEquals(
            "정상적으로 데이터가 들어왔을 시",
            true,
            viewModel.searchList.value?.isNotEmpty()
        )
    }

    @Test
    fun `전체 데이터에서 Blog 리스트가 비어있을 때`() {
        // given
        Mockito.`when`(service.getBlog("테스트", "accuracy", 1, 25))
            .thenReturn(Single.just(emptySearchModel()))
        Mockito.`when`(service.getCafe("테스트", "accuracy", 1, 25))
            .thenReturn(Single.just(successSearchModel()))

        // when
        viewModel.getAllData("테스트", "accuracy", 25, true)

        // then
        println("${viewModel.searchList.value}")
        Assert.assertEquals(
            "전체 데이터에서 Blog 리스트가 비어있을 때",
            true,
            viewModel.searchList.value?.isNotEmpty()
        )
    }

    private fun successSearchModel(): SearchModel? {
        val json = "{\n" +
                "    \"documents\": [\n" +
                "        {\n" +
                "            \"blogname\": \"원더바웃\",\n" +
                "            \"contents\": \"애드센스 본문 상단 광고 2개 끝 --&gt; 아래 이미지에 표시된 부분을 자신의 코드로 수정해 주세요. [##_Image|kage@kHVI8/btqL8mcRR16/L0RENf8Jb6JcO4ksa8<b>wQEK</b>/img.png|alignCenter|width=&#34;100%&#34; alt=&#34;광고코드 수정&#34; data-origin-width=&#34;0&#34; data-origin-height=&#34;0&#34; data-ke-mobilestyle=&#34;widthContent&#34;|||_##] 본문 우측...\",\n" +
                "            \"datetime\": \"2020-10-30T15:21:58.000+09:00\",\n" +
                "            \"thumbnail\": \"https://search3.kakaocdn.net/argon/130x130_85_c/IqosXiLlSd1\",\n" +
                "            \"title\": \"티스토리 상단광고 2개, 우측상단 광고등(스킨편집 html에 광고 넣기)\",\n" +
                "            \"url\": \"http://wonderbout.tistory.com/165\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"blogname\": \"84kexldyxlnf9님의블로그\",\n" +
                "            \"contents\": \"bbD7cy6Pafb6klJaJCL4Oeiq3oNjVFG6Rje3SkT3A59VqT59<b>wQek</b>5xK8VysRd8Y4sc8lx2o77x2xq7HXYWh4t2cpAEgNNfSgr0BZ\",\n" +
                "            \"datetime\": \"2018-01-11T14:50:00.000+09:00\",\n" +
                "            \"thumbnail\": \"https://search4.kakaocdn.net/argon/130x130_85_c/D9Ergl7dwso\",\n" +
                "            \"title\": \"1450V49CPoTBlbteBO5ngxRm\",\n" +
                "            \"url\": \"http://blog.naver.com/84kexldyxlnf9/221182808900\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"meta\": {\n" +
                "        \"is_end\": false,\n" +
                "        \"pageable_count\": 6,\n" +
                "        \"total_count\": 6\n" +
                "    }\n" +
                "}"
        return Gson().fromJson(json, SearchModel::class.java)
    }

    private fun emptySearchModel(): SearchModel? {
        val json = "{\n" +
                "    \"documents\": [],\n" +
                "    \"meta\": {\n" +
                "        \"is_end\": true,\n" +
                "        \"pageable_count\": 0,\n" +
                "        \"total_count\": 0\n" +
                "    }\n" +
                "}"
        return Gson().fromJson(json, SearchModel::class.java)
    }


}