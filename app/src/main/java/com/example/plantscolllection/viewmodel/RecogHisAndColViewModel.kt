package com.example.plantscolllection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.interfaces.callback.PlantsInfoCallback
import com.example.plantscolllection.repository.RecogHisAndColRepository
import kotlinx.coroutines.launch

class RecogHisAndColViewModel:ViewModel() {

    private val recogHisAndColRepository by lazy { RecogHisAndColRepository() }
    //获取识别历史
    fun getRecognitionHistory(token:String,door:String,pageNo:Int,pageSize:Int, plantsInfoCallback: PlantsInfoCallback){
        viewModelScope.launch {
            val baseBean = recogHisAndColRepository.getRecognitionHistory(token,door,pageNo,pageSize)
            if(baseBean.code==200){
                baseBean.data?.let { plantsInfoCallback.getPlantsInfoSuccess(it) }
            }else{
                baseBean.message.let { plantsInfoCallback.getPlantsInfoFailed(it) }
            }
        }
    }
    //获取我的收藏
    fun getCollectionPlants(token:String,door:String,pageNo:Int,pageSize:Int, plantsInfoCallback: PlantsInfoCallback){
        viewModelScope.launch {
            val baseBean = recogHisAndColRepository.getCollectionPlants(token,door,pageNo,pageSize)
            if(baseBean.code==200){
                baseBean.data?.let { plantsInfoCallback.getPlantsInfoSuccess(it) }
            }else{
                baseBean.message.let { plantsInfoCallback.getPlantsInfoFailed(it) }
            }
        }
    }

}