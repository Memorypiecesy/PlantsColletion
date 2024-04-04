package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.bean.PlantsInfo

interface PlantsInfoCallback {
    fun getPlantsInfoSuccess(plantsInfoList: List<PlantsInfo>)
    fun getPlantsInfoFailed(errorMessage: String)
}