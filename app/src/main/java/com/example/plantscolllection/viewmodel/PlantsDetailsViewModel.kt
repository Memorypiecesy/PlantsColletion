package com.example.plantscolllection.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.interfaces.callback.PlantsDetailsCallback
import com.example.plantscolllection.repository.PlantsDetailsRepository
import kotlinx.coroutines.launch
import kotlin.math.log

class PlantsDetailsViewModel:ViewModel() {

    private val plantsDetailsRepository by lazy { PlantsDetailsRepository() }

    companion object{
        private const val TAG = "PlantsDetailsViewModel"
    }

    fun getPlantsDetails(id:Int, plantsDetailsCallback: PlantsDetailsCallback) {
        viewModelScope.launch {
            val baseBean = plantsDetailsRepository.getPlantsDetails(id)
            if(baseBean.code==200){
                baseBean.data?.let { plantsDetailsCallback.getPlantsDetailsSuccess(it) }
            }else{
                baseBean.message.let { plantsDetailsCallback.getPlantsDetailsFailed(it) }
            }
        }
    }

}