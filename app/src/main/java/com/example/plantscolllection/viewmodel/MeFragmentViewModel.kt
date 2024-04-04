package com.example.plantscolllection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.interfaces.callback.UserInfoCallback
import com.example.plantscolllection.repository.MeFragmentRepository
import kotlinx.coroutines.launch

class MeFragmentViewModel:ViewModel() {

    private val meFragmentRepository by lazy { MeFragmentRepository() }

    companion object{
        const val TAG = "MeFragmentViewModel"
    }

    fun getUserInfo(token:String,userInfoCallback: UserInfoCallback){
        viewModelScope.launch {
            val baseBean = meFragmentRepository.getUserInfo(token)
            if(baseBean.code==200){
                baseBean.data?.let { userInfoCallback.getUserInfoSuccess(it) }
            }else{
                baseBean.message.let { userInfoCallback.getUserInfoFailed(it) }
            }
        }
    }

}