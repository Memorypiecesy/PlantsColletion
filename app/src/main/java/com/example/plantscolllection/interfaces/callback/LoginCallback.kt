package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.BaseBean

interface LoginCallback {
    fun loginSuccess(token : String)
    fun loginFailed(errorMessage : String)
}