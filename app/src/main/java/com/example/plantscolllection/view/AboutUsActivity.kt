package com.example.plantscolllection.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivityAboutUsBinding
import com.example.plantscolllection.utils.Utils

class AboutUsActivity : AppCompatActivity() {

    private lateinit var activityAboutUsBinding: ActivityAboutUsBinding

    private val countDownTimer = object : CountDownTimer(1000,1000){
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            end()   //开启当前已是最新版本CardView平移结束动画
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityAboutUsBinding = DataBindingUtil.setContentView(this,R.layout.activity_about_us)
        activityAboutUsBinding.lifecycleOwner = this
        initView()
    }

    private fun initView() {
        //"功能介绍"的RelativeLayout点击事件
        activityAboutUsBinding.checkNewVersionRelative.setOnClickListener {
            Toast.makeText(this@AboutUsActivity,"该功能暂未开发",Toast.LENGTH_SHORT).show()
        }
        //"检查新版本"的RelativeLayout点击事件
        activityAboutUsBinding.checkNewVersionRelative.setOnClickListener {
            start() //开启当前已是最新版本CardView平移开始动画
            countDownTimer.start() //计时，时间结束后开启当前已是最新版本CardView平移结束动画
        }
        //返回键
        activityAboutUsBinding.backImage.setOnClickListener {
            finish()
        }
    }
    //当前已是最新版本CardView平移开始动画
    private fun start() {
        // 显示控件
        activityAboutUsBinding.alreadyLatestVersionCard.visibility = View.VISIBLE

        //开启平移动画
        val startTranslateAnim = TranslateAnimation(0F, 0F, activityAboutUsBinding.alreadyLatestVersionCard.height*1.0F/8, 0F)
        startTranslateAnim.duration = 200

        //控件开始动画
        activityAboutUsBinding.alreadyLatestVersionCard.startAnimation(startTranslateAnim);

    }
    //当前已是最新版本CardView平移结束动画
    private fun end() {

        //关闭平移动画
        val endTranslateAnim = TranslateAnimation(0F, 0F, 0F, activityAboutUsBinding.alreadyLatestVersionCard.height*1.0F/8)
        endTranslateAnim.duration = 200

        //控件开始动画
        activityAboutUsBinding.alreadyLatestVersionCard.startAnimation(endTranslateAnim)

        endTranslateAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                // 隐藏控件
                activityAboutUsBinding.alreadyLatestVersionCard.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }
}