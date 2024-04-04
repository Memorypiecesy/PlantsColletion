package com.example.plantscolllection.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivitySpecimenRetrieveBinding

class SpecimenRetrieveActivity : AppCompatActivity() {

    private lateinit var activitySpecimenRetrieveBinding: ActivitySpecimenRetrieveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySpecimenRetrieveBinding = DataBindingUtil.setContentView(this, R.layout.activity_specimen_retrieve)
        activitySpecimenRetrieveBinding.lifecycleOwner = this
        initView()
    }

    private fun initView() {
        //返回键
        activitySpecimenRetrieveBinding.backImage.setOnClickListener {
            finish()
        }
        loadResultView("https://www.cvh.ac.cn/")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadResultView(URL: String) {
        activitySpecimenRetrieveBinding.specimenRetrieveWebView.settings.javaScriptEnabled = true //设置WebView属性,运行执行js脚本
        activitySpecimenRetrieveBinding.specimenRetrieveWebView.loadUrl(URL) //设置网址
        activitySpecimenRetrieveBinding.specimenRetrieveWebView.webViewClient = WebViewClient()
        val webSettings: WebSettings = activitySpecimenRetrieveBinding.specimenRetrieveWebView.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true

        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING //支持内容重新布局

        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        webSettings.textZoom = 2 //设置文本的缩放倍数，默认为 100
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH) //提高渲染的优先级
        webSettings.standardFontFamily = "" //设置 WebView 的字体，默认字体为 "sans-serif"
        webSettings.defaultFontSize = 20 //设置 WebView 字体的大小，默认大小为 16
        webSettings.minimumFontSize = 12 //设置 WebView 支持的最小字体大小，默认为 8
    }
}