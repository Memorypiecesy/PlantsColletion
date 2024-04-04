package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.HotBotany

interface HotBotanyCallback {
    fun getHotBotanySuccess(hotBotanyResList: List<HotBotany>)
    fun getHotBotanyFailed(errorMessage: String)
}