package com.example.plantscolllection.bean

class Weather {
    lateinit var province: String
    lateinit var city: String
    lateinit var adcode: String
    lateinit var windpower: String
    lateinit var weather: String
    lateinit var temperature: String
    lateinit var humidity: String
    lateinit var reporttime: String
    lateinit var winddirection: String
    override fun toString(): String {
        return "Weather(province='$province', city='$city', adcode='$adcode', windpower='$windpower', weather='$weather', temperature='$temperature', humidity='$humidity', reporttime='$reporttime', winddirection='$winddirection')"
    }

}