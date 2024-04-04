package com.example.plantscolllection.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivityRetrieveResultBinding
import com.example.plantscolllection.utils.Utils


class RetrieveResultActivity : AppCompatActivity() {

    private lateinit var activityRetrieveResultBinding: ActivityRetrieveResultBinding
    private var plantsRetrieve:String? = ""
    private val WEB_URL = "http://www.iplant.cn/z/list?keyword="
    private val PIC_URL = "http://mpb.iplant.cn/tu/"
    private var exitTime: Long = 0

    companion object{
        const val TAG = "RetrieveResultActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityRetrieveResultBinding = DataBindingUtil.setContentView(this,R.layout.activity_retrieve_result)
        activityRetrieveResultBinding.lifecycleOwner = this
        plantsRetrieve = intent.getStringExtra("plants_retrieve")
        loadResultView(WEB_URL+plantsRetrieve)
        Log.d(TAG, "RetrieveResultActivity处检索的url-->${WEB_URL+plantsRetrieve}")
        initView()
    }




    private fun initView() {
        //返回键
        activityRetrieveResultBinding.backImage.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadResultView(URL: String) {
        activityRetrieveResultBinding.webView.settings.javaScriptEnabled = true //设置WebView属性,运行执行js脚本
        activityRetrieveResultBinding.webView.loadUrl(URL) //设置网址
        activityRetrieveResultBinding.webView.webViewClient = WebViewClient()
        val webSettings: WebSettings = activityRetrieveResultBinding.webView.settings
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

    //我们需要重写回退按钮的时间,当用户点击回退按钮：
    //1.webView.canGoBack()判断网页是否能后退,可以则goback()
    //2.如果不可以连续点击两次退出App,否则弹出提示Toast
    override fun onBackPressed() {
        if (activityRetrieveResultBinding.webView.canGoBack()) {
            activityRetrieveResultBinding.webView.goBack()
        } else {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(
                    applicationContext, "再按一次返回退出",
                    Toast.LENGTH_SHORT
                ).show()
                exitTime = System.currentTimeMillis()
            } else {
                //super.onBackPressed();
                finish()
            }
        }
    }


}