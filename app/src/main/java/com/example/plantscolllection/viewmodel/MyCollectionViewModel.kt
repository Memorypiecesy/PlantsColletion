package com.example.plantscolllection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.PlantsInfo
import com.example.plantscolllection.repository.RecogHisAndColRepository
import kotlinx.coroutines.launch

class MyCollectionViewModel:ViewModel() {

    private val mRecogHisAndColRepository by lazy { RecogHisAndColRepository() }
    private val _recognition by lazy { MutableLiveData<List<PlantsInfo>>() }
    val mPlantsInfoLiveData:LiveData<List<PlantsInfo>> = _recognition

    fun getRecognitionHistory(token:String,door:String,pageNo:Int,pageSize:Int){
        viewModelScope.launch {
            val baseBean = mRecogHisAndColRepository.getRecognitionHistory(token,door,pageNo,pageSize)
            _recognition.value = baseBean.data
        }
    }

}