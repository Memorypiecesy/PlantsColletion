package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.bean.PlantsDetails
import com.example.plantscolllection.bean.PlantsInfo

interface PlantsDetailsCallback {
    fun getPlantsDetailsSuccess(plantsDetails: PlantsDetails)
    fun getPlantsDetailsFailed(errorMessage: String)
}