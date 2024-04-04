package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.Login
import com.example.plantscolllection.client.RetrofitClient

class LoginRepository {

    suspend fun login(phone:String,password:String): BaseBean<String> {
        val login = Login()
        login.phone=phone
        login.password=password
        return RetrofitClient.retrofitApi.login(login)
    }

}