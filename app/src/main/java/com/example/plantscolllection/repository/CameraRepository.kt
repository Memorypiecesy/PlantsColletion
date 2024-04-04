package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.BotanyRes
import com.example.plantscolllection.client.RetrofitClient
import okhttp3.MultipartBody
import java.io.File

class CameraRepository {
    //上传图片
    suspend fun postImage(part: MultipartBody.Part): BaseBean<String> {
        return RetrofitClient.retrofitApi.postImage(part)
    }
    //获取识别结果
    suspend fun getBotanyRes(token:String, filePath:String):BaseBean<List<BotanyRes>>{
        return RetrofitClient.retrofitApi.getBotanyRes(token, filePath)
    }
    //收藏植物
    suspend fun collectPlants(token:String, id:Int): BaseBean<String> {
        return RetrofitClient.retrofitApi.collectPlants(token, id)
    }
}