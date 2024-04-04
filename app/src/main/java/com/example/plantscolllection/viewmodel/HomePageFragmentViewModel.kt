package com.example.plantscolllection.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.RequestParm
import com.example.plantscolllection.interfaces.callback.NoteCallbackContract
import com.example.plantscolllection.interfaces.callback.WeatherCallback
import com.example.plantscolllection.repository.HomePageFragmentRepository
import com.example.plantscolllection.repository.MyNoteFragmentRepository
import kotlinx.coroutines.launch

class HomePageFragmentViewModel:ViewModel() {

    private val homePageFragmentRepository by lazy { HomePageFragmentRepository() }

    companion object{
        const val TAG = "MyNoteFragmentViewModel"
    }

    fun getNotes(token:String,pageNo:Int,pageSize:Int,noteCallback: NoteCallbackContract.NotesCallback){
        viewModelScope.launch {
            val baseBean = homePageFragmentRepository.getNotes(token,pageNo,pageSize)
            if(baseBean.code==200){

                baseBean.data?.let { noteCallback.getNotesSuccess(it) }
            }else{
                baseBean.message.let { noteCallback.getNotesFailed(it) }
            }
        }
    }

    fun getWeather(weatherCallback: WeatherCallback){
        viewModelScope.launch {
            val baseBean = homePageFragmentRepository.getWeather()
            if(baseBean.code==200){
                baseBean.data?.let { weatherCallback.getWeatherSuccess(it) }
            }else{
                baseBean.message.let { weatherCallback.getWeatherFailed(it) }
            }
        }
    }

}