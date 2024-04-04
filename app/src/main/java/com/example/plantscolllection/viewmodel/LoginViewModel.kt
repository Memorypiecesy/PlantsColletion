package com.example.plantscolllection.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.interfaces.callback.LoginCallback
import com.example.plantscolllection.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val loginRepository by lazy { LoginRepository() }


    fun login(phone:String,password:String, loginCallback: LoginCallback){
        viewModelScope.launch {
            val baseBean = loginRepository.login(phone,password)
            if(baseBean.code==200){
                baseBean.data?.let { loginCallback.loginSuccess(it) }
            }else{
                baseBean.message.let { loginCallback.loginFailed(it) }
            }
        }

    }
}