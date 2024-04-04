package com.example.plantscolllection.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.interfaces.callback.IdentificationResultCallback
import com.example.plantscolllection.repository.CameraRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class HomeActivityViewModel() : ViewModel() {

    val isBottomRelativeShowedLiveData by lazy { MutableLiveData<Boolean>() }
    //Fragment的item操作通知给Activity全选圆圈的LiveData
    val isAllSelectedImageSelectedForActivity by lazy { MutableLiveData<Boolean>() }
    //Activity全选圆圈的操作通知给Fragment的item的LiveData
    val isAllSelectedImageSelectedForFragment by lazy { MutableLiveData<Boolean>() }
    val isDeleteButtonEnabled by lazy { MutableLiveData<Boolean>() }
    val isDeleteNotes by lazy { MutableLiveData<Boolean>() } //是否删除笔记的LiveData
    val hideDeletingNoteDialog by lazy { MutableLiveData<Boolean>() } //隐藏"删除笔记中"弹窗的LiveData
    val showDeleteSingleNoteWarnDialog by lazy { MutableLiveData<Boolean>() } //显示"删除单个笔记警告"弹窗的LiveData

    companion object{
        const val TAG = "HomeActivityViewModel"
    }

}