package com.example.plantscolllection.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.interfaces.callback.IdentificationResultCallback
import com.example.plantscolllection.repository.CameraRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val cameraRepository by lazy { CameraRepository() }
    private val sp by lazy { application.getSharedPreferences("data", Context.MODE_PRIVATE) }

    companion object{
        const val TAG = "CameraViewModel"
    }
    //上传图片顺便获取植物识别结果
    fun postImage(part: MultipartBody.Part, identificationResultCallback: IdentificationResultCallback) {
        viewModelScope.launch {
            val baseBean = cameraRepository.postImage(part)
            if (baseBean.code == 200) {
                baseBean.data?.let {filePath->
                    sp.getString("token","no such key")?.let { token ->
                        Log.d(TAG, "返回的filePath-->${filePath}")
                        val botanyResBaseBean = cameraRepository.getBotanyRes(token, filePath) //获得BotanyRes集合对应的BaseBean对象
                        if (botanyResBaseBean.code==200){
                            botanyResBaseBean.data?.let {botanyRes->
                                Log.d(TAG, "CameraViewModel处得到的BotanyRes集合-->${botanyRes}")
                                identificationResultCallback.getIdentificationResultSuccess(botanyRes)
                            }
                        }else{
                            botanyResBaseBean.message.let {errorMessage->
                                Log.d(TAG, "CameraViewModel处获取BotanyRes集合失败-->${errorMessage}")
                                identificationResultCallback.getIdentificationResultFailed(errorMessage)
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
    //收藏植物
    fun collectPlants(token:String, id:Int) {
        Log.d(TAG, "PlantsDetailsViewModel处collectPlants方法调用")
        viewModelScope.launch {
            val baseBean = cameraRepository.collectPlants(token,id)
            Log.d(TAG, "PlantsDetailsViewModel处collectPlants方法结果-->${baseBean.data}")
        }
    }

}