package com.example.plantscolllection.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.User
import com.example.plantscolllection.interfaces.callback.UpdateUserInfoCallback
import com.example.plantscolllection.repository.InfoSettingRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class InfoSettingViewModel(application: Application) : AndroidViewModel(application) {

    private val infoSettingRepository by lazy { InfoSettingRepository() }
    private val sp by lazy { application.getSharedPreferences("data", Context.MODE_PRIVATE) }

    companion object{
        const val TAG = "InfoSettingViewModel"
    }


    //上传图片顺便更新用户信息
    fun postImageAndUpdateUserInfo(part: MultipartBody.Part, user: User, updateUserInfoCallback: UpdateUserInfoCallback) {
        viewModelScope.launch {
            val baseBean = infoSettingRepository.postImage(part)
            if (baseBean.code == 200) {
                baseBean.data?.let {filePath->
                    sp.getString("token","no such key")?.let { token ->
                        Log.d(TAG, "返回的filePath-->${filePath}")
                        user.photo=filePath //给要上传的User对象赋值返回来的图片地址
                        val updateResultBaseBean = infoSettingRepository.updateUserInfo(token, user)
                        if (updateResultBaseBean.code==200){
                            updateResultBaseBean.data?.let {updateResult->
                                updateUserInfoCallback.updateUserInfoSuccess(updateResult)
                            }
                        }else{
                            updateResultBaseBean.message.let {errorMessage->
                                updateUserInfoCallback.updateUserInfoFailed(errorMessage)
                            }
                        }
                    }
                }
            } else {
                baseBean.data?.let {errorMessage->
                    Log.d(TAG, "上传图片失败-->${errorMessage}")
                }
            }
        }

    }

    //用户没有更新头像的用户信息更新
    fun updateUserInfoWithOriginalAvatar(user: User, updateUserInfoCallback: UpdateUserInfoCallback){
        viewModelScope.launch {
            sp.getString("token","no such key")?.let { token ->
                val updateResultBaseBean = infoSettingRepository.updateUserInfo(token, user)
                if (updateResultBaseBean.code==200){
                    updateResultBaseBean.data?.let {updateResult->
                        updateUserInfoCallback.updateUserInfoSuccess(updateResult)
                    }
                }else{
                    updateResultBaseBean.message.let {errorMessage->
                        updateUserInfoCallback.updateUserInfoFailed(errorMessage)
                    }
                }
            }
        }

    }
}