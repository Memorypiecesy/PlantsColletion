package com.example.plantscolllection.interfaces.callback

interface UpdateUserInfoCallback {
    fun updateUserInfoSuccess(updateSuccess: String)
    fun updateUserInfoFailed(errorMessage: String)
}