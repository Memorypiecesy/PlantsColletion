package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.BotanyRes

interface IdentificationResultCallback {
    fun getIdentificationResultSuccess(botanyResList: List<BotanyRes>)
    fun getIdentificationResultFailed(errorMessage: String)
}