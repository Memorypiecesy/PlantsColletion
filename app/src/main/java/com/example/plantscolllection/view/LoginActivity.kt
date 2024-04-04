package com.example.plantscolllection.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivityLoginBinding
import com.example.plantscolllection.interfaces.callback.LoginCallback
import com.example.plantscolllection.utils.SHA1
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var activityLoginBinding: ActivityLoginBinding
    private val spEditor by lazy { getSharedPreferences("data",Context.MODE_PRIVATE).edit() }
    private val loginViewModel by viewModels<LoginViewModel>()
    private val shakeAnimation by lazy { AnimationUtils.loadAnimation(this,R.anim.shake) }
    private val loginCallback by lazy {
        object : LoginCallback{
            override fun loginSuccess(token: String) {
                Log.d(TAG, "登陆成功，token->${token}")
                spEditor.putString("token",token)
                spEditor.apply()
                jumpToHomeActivity()
            }

            override fun loginFailed(errorMessage: String) {
                Toast.makeText(this@LoginActivity,"登陆失败-->${errorMessage}",Toast.LENGTH_SHORT).show()
                Log.d(TAG, "登陆失败-->${errorMessage}")
            }

        }
    }

    companion object{
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        Thread.sleep(3000)
        setTheme(R.style.Theme_PlantsColllection)
        super.onCreate(savedInstanceState)
        printSHA1()
        Utils.setStatusBar(this,false)
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        activityLoginBinding.lifecycleOwner = this
        initView()
    }

    private fun initView() {
        //让文字变色
        val content = "我已阅读并同意《服务条款》和《隐私协议》"
        val spannable = SpannableStringBuilder(content).apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#FF4CDB9A")),7,13,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.parseColor("#FF4CDB9A")),14,20,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        activityLoginBinding.loginText.text=spannable
        //同意条款文字左边的勾选圈圈点击事件
        activityLoginBinding.loginCircle.setOnClickListener {
            activityLoginBinding.loginCircle.isSelected=!activityLoginBinding.loginCircle.isSelected
        }
        //"本机号码一键登录"按钮点击事件
        activityLoginBinding.thisPhoneNumberCardView.setOnClickListener {
//            loginViewModel.login("123456","000000",loginCallback)

//            if(activityLoginBinding.loginCircle.isSelected){//如果已勾选同意条款才跳转
//                loginViewModel.login("123456","000000",loginCallback)
//                jumpToHomeActivity()
//            }else{//否则文字振动
//                activityLoginBinding.loginText.startAnimation(shakeAnimation)
//                Toast.makeText(this@LoginActivity,"请先同意条款与协议！",Toast.LENGTH_SHORT).show()
//            }
            jumpToHomeActivity()

        }
    }

    private fun jumpToHomeActivity(){
        val intent = Intent(this@LoginActivity,HomeActivity::class.java)
        startActivity(intent)
    }

    //输出app的SHA1的方法
    private fun printSHA1() {
        Log.d("MainActivity", "SHA1->${SHA1.sHA1(this)}")

    }
}