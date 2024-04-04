package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.bean.User

interface UserInfoCallback {
    fun getUserInfoSuccess(user: User)
    fun getUserInfoFailed(errorMessage: String)
}