package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.PlantsInfo
import com.example.plantscolllection.client.RetrofitClient

class MyCollectionRepository {

    suspend fun getRecognitionHistory(token:String,door:String,pageNo:Int,pageSize:Int): BaseBean<List<PlantsInfo>> {
        return RetrofitClient.retrofitApi.getRecognitionHistory(token,door,pageNo,pageSize)
    }
}