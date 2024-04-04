package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.Botanysearch
import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.bean.PlantsInfo

interface BotanySearchCallback {
    fun getBotanySearchSuccess(botanySearchList: List<Botanysearch>)
    fun getBotanySearchFailed(errorMessage: String)
}