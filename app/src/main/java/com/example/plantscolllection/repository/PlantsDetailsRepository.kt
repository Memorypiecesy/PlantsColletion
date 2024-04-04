package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.PlantsDetails
import com.example.plantscolllection.client.RetrofitClient

class PlantsDetailsRepository {

    suspend fun getPlantsDetails(id:Int): BaseBean<PlantsDetails> {
        return RetrofitClient.retrofitApi.getPlantsDetails(id)
    }

}