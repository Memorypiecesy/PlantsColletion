package com.example.plantscolllection.interfaces.callback

interface TextExpandCallback {
    fun getTextExpandListSuccess(textExpandList: List<String>)
    fun getTextExpandListFailed(errorMessage: String)
}