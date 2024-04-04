package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.bean.Login
import com.example.plantscolllection.bean.User
import com.example.plantscolllection.client.RetrofitClient
import okhttp3.MultipartBody

class InfoSettingRepository {

    //上传图片
    suspend fun postImage(part: MultipartBody.Part): BaseBean<String> {
        return RetrofitClient.retrofitApi.postImage(part)
    }
    //更新用户信息
    suspend fun updateUserInfo(token:String, user: User):BaseBean<String>{
        return RetrofitClient.retrofitApi.updateUserInfo(token,user)
    }

}