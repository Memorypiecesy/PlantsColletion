package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.Login
import com.example.plantscolllection.bean.User
import com.example.plantscolllection.client.RetrofitClient

class MeFragmentRepository {

    suspend fun getUserInfo(token:String): BaseBean<User> {
        return RetrofitClient.retrofitApi.getUserInfo(token)
    }

}