package com.example.plantscolllection.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.HomePageNoteAdapter
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.bean.Weather
import com.example.plantscolllection.databinding.FragmentHomePageBinding
import com.example.plantscolllection.interfaces.callback.NoteCallbackContract
import com.example.plantscolllection.interfaces.callback.WeatherCallback
import com.example.plantscolllection.viewmodel.HomePageFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomePageFragment : Fragment() {

    private lateinit var fragmentHomePageBinding: FragmentHomePageBinding
    private val homePageFragmentViewModel by viewModels<HomePageFragmentViewModel>()
    private val homePageNoteAdapter by lazy { HomePageNoteAdapter(requireActivity()) }
    private val sp by lazy { requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE) }
    private var pageNoThis = 0
    private var pageSizeThis = 10

    //天气回调接口
    private val weatherCallback by lazy {
        object : WeatherCallback {

            override fun getWeatherSuccess(weatherList: List<Weather>) {
                Log.d(TAG, "HomePageFragment处获取天气成功->${weatherList}")
                val weatherBean = weatherList[0]
                fragmentHomePageBinding.regionText.text=weatherBean.city
                fragmentHomePageBinding.temperatureText.text="${weatherBean.temperature}°C"
                fragmentHomePageBinding.weatherText.text=weatherBean.weather
                fragmentHomePageBinding.dateText.text=timeFormat(timeFormat(weatherBean.reporttime))
                fragmentHomePageBinding.weatherImage.setImageResource(getWeatherIcon(weatherBean.weather))
                fragmentHomePageBinding.background.setImageResource(getBackground(weatherBean.weather))
            }

            override fun getWeatherFailed(errorMessage: String) {
                Log.d(TAG, "HomePageFragment处获取天气失败->${errorMessage}")
            }

        }
    }
    //获得笔记回调接口
    private val getNotesCallback by lazy {
        object : NoteCallbackContract.NotesCallback {

            override fun getNotesSuccess(noteVoList: List<NoteVo>) {
                Log.d(TAG, "HomePageFragment处获取笔记列表成功->${noteVoList}")
                homePageNoteAdapter.setHomePageNoteList(noteVoList)
            }

            override fun getNotesFailed(errorMessage: String) {
                Log.d(TAG, "HomePageFragment处获取笔记列表失败->${errorMessage}")
            }

        }
    }

    companion object {
        private const val TAG = "HomePageFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        activity?.let {
            it.window.statusBarColor = Color.TRANSPARENT
        }
        fragmentHomePageBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home_page,container,false)
        return fragmentHomePageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        homePageFragmentViewModel.getWeather(weatherCallback) //请求天气
        //请求笔记
//        sp.getString("token","no this key")?.let {token->
//            homePageFragmentViewModel.getNotes(token,pageNoThis,pageSizeThis,getNotesCallback)
//        }
        //搜索框点击跳转到搜索页
        fragmentHomePageBinding.homePageSearchCardView.setOnClickListener {
            val intent = Intent(requireActivity(),SearchActivity::class.java)
            startActivity(intent)
        }
        //分类检索icon点击跳转
        fragmentHomePageBinding.classificationRetrieveImage.setOnClickListener {
            val intent = Intent(requireActivity(),PlantsClassifyActivity::class.java)
            startActivity(intent)
        }
        //标本检索icon点击跳转
        fragmentHomePageBinding.specimenRetrieveImage.setOnClickListener {
            val intent = Intent(requireActivity(),SpecimenRetrieveActivity::class.java)
            startActivity(intent)
        }
        //志书检索icon点击跳转
        fragmentHomePageBinding.annalsRetrieveImage.setOnClickListener {
            val intent = Intent(requireActivity(),AnnalsRetrieveActivity::class.java)
            startActivity(intent)
        }
        //配置笔记RecyclerView
        fragmentHomePageBinding.homePageRecycler.apply {
            adapter=homePageNoteAdapter
            layoutManager=LinearLayoutManager(requireActivity())
        }
    }
    //根据天气状况返回对应的背景图片
    private fun getBackground(weatherDescription:String):Int{
        return when(weatherDescription){
            "多云转晴","晴"->R.drawable.home_page_sun_background
            "多云"->R.drawable.home_page_cloudy_background
            "阴天","阴"->R.drawable.home_page_overcast_background
            "小雨"->R.drawable.home_page_small_rain_background
            "雷阵雨"->R.drawable.home_page_thunder_shower_background
            "大雨"->R.drawable.home_page_heavy_rain_background
            else->R.drawable.home_page_sun_background
        }
    }
    //根据天气状况返回对应的天气icon
    private fun getWeatherIcon(weatherDescription:String):Int{
        return when(weatherDescription){
            "多云转晴","晴"->R.drawable.home_page_sun_icon
            "多云"->R.drawable.home_page_cloudy_icon
            "阴天","阴"->R.drawable.home_page_overcast_icon
            "小雨"->R.drawable.home_page_small_rain_icon
            "雷阵雨"->R.drawable.home_page_thunder_shower_icon
            "大雨"->R.drawable.home_page_heavy_rain_icon
            else->R.drawable.home_page_sun_icon
        }
    }
    //解析时间
    private fun timeFormat(time:String):String{
        val simpleDateFormat = SimpleDateFormat("MM/dd")
        val time: String = simpleDateFormat.format(System.currentTimeMillis())
        val weekDays = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val cal: Calendar = Calendar.getInstance()
        cal.time=Date()
        var w: Int = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0) w = 0
        return weekDays[w] + "," + time
    }
}