package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.bean.Weather

interface WeatherCallback {
    fun getWeatherSuccess(weatherList: List<Weather>)
    fun getWeatherFailed(errorMessage: String)
}