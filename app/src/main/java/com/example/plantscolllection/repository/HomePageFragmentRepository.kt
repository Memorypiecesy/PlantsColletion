package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.bean.RequestParm
import com.example.plantscolllection.bean.Weather
import com.example.plantscolllection.client.RetrofitClient

class HomePageFragmentRepository {
    //获得笔记
    suspend fun getNotes(token:String,pageNo:Int,pageSize:Int): BaseBean<List<NoteVo>> {
        return RetrofitClient.retrofitApi.getNotes(token,pageNo,pageSize)
    }
    //请求天气
    suspend fun getWeather():BaseBean<List<Weather>>{
        return RetrofitClient.retrofitApi.getWeather()
    }

}