package com.example.plantscolllection.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.PlantsInfoAdapter
import com.example.plantscolllection.bean.PlantsInfo
import com.example.plantscolllection.databinding.ActivityRecogHisAndColBinding
import com.example.plantscolllection.interfaces.callback.PlantsInfoCallback
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.RecogHisAndColViewModel

class RecogHisAndColActivity : AppCompatActivity() {

    private lateinit var activityRecogHisAndColBinding: ActivityRecogHisAndColBinding
    private val mPlantsInfoAdapter by lazy { PlantsInfoAdapter(this) }
    private val mRecogHisAndColViewModel by viewModels<RecogHisAndColViewModel>()
    private val sp by lazy { getSharedPreferences("data", Context.MODE_PRIVATE) }
    private var pageNo=1
    private var pageSize=20
    private var height=0
    //0代表识别历史Activity，1代表我的收藏Activity
    private var type=-1

    private val plantsInfoCallback by lazy {
        object : PlantsInfoCallback {
            override fun getPlantsInfoSuccess(plantsInfoList: List<PlantsInfo>) {
                Log.d(TAG, "RecogHisAndColActivity处获取植物信息成功->${plantsInfoList},type类型-->${type}")
                mPlantsInfoAdapter.setPlantsInfoList(plantsInfoList)
            }

            override fun getPlantsInfoFailed(errorMessage: String) {
                Log.d(TAG, "RecogHisAndColActivity处获取植物信息失败->${errorMessage},type类型-->${type}")
            }


        }
    }

    companion object{
        private const val TAG = "RecogHisAndColActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityRecogHisAndColBinding = DataBindingUtil.setContentView(this,R.layout.activity_recog_his_and_col)
        activityRecogHisAndColBinding.lifecycleOwner = this
        type = intent.getIntExtra("activity_type",-1)
        initView()
        //获取组件高度
        initOnPreDrawListener()
        Log.d(TAG, "RecogHisAndColActivity的onCreate方法")
        //判断当前Activity类型后发送请求
        sendRequest("0")
    }

    private fun initView() {
        //根据当前是识别历史Activity还是我的收藏Activity设置最上面的文字
        activityRecogHisAndColBinding.textAbove.text = if (type==0) "识别历史" else "我的收藏"
        //配置识别历史的RecyclerView
        activityRecogHisAndColBinding.recognitionHistoryRecycler.apply {
            layoutManager=LinearLayoutManager(this@RecogHisAndColActivity)
            adapter=mPlantsInfoAdapter
        }
        //返回键点击事件
        activityRecogHisAndColBinding.backImage.setOnClickListener {
            finish()
        }
        //"按门类检索"范围内点击事件
        activityRecogHisAndColBinding.retrieveByDoorClickArea.setOnClickListener {
            if (activityRecogHisAndColBinding.retrieveByDoorLinear.visibility== View.GONE){
                start()
                Log.d(TAG, "开始动画")
            }else{
                end()
                Log.d(TAG, "结束动画")
            }
        }
        //给六个RelativeLayout设置点击事件
        setTextViewOnClick()
    }

    private fun initOnPreDrawListener() {
        val viewTreeObserver = window.decorView.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                height=activityRecogHisAndColBinding.retrieveByDoorLinear.measuredHeight
                // 移除OnPreDrawListener事件监听
                this@RecogHisAndColActivity.window.decorView.viewTreeObserver.removeOnPreDrawListener(this)
                //获取完高度后隐藏控件
                activityRecogHisAndColBinding.retrieveByDoorLinear.visibility= View.GONE
                Log.d(TAG, "组件高度->${height}")
                return true
            }
        })
    }

    private fun start() {
        // 显示控件
        activityRecogHisAndColBinding.retrieveByDoorLinear.visibility = View.VISIBLE

        //开启平移动画
        val startTranslateAnim = TranslateAnimation(0F, 0F, -height*1.0F, 0F)
        startTranslateAnim.duration = 500

        //控件开始动画
        activityRecogHisAndColBinding.retrieveByDoorLinear.startAnimation(startTranslateAnim);

    }

    private fun end() {

        //关闭平移动画
        val endTranslateAnim = TranslateAnimation(0F, 0F, 0F, -height*1.0F)
        endTranslateAnim.duration = 500

        //控件开始动画
        activityRecogHisAndColBinding.retrieveByDoorLinear.startAnimation(endTranslateAnim)

        endTranslateAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                // 隐藏控件
                activityRecogHisAndColBinding.retrieveByDoorLinear.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }

    //给六个RelativeLayout设置点击事件
    private fun setTextViewOnClick() {
        activityRecogHisAndColBinding.phycophytaRelative.setOnClickListener {
            afterClickTextView("藻类植物门")
        }
        activityRecogHisAndColBinding.lichensRelative.setOnClickListener {
            afterClickTextView("地衣植物门")
        }
        activityRecogHisAndColBinding.bryophytaRelative.setOnClickListener {
            afterClickTextView("苔藓植物门")
        }
        activityRecogHisAndColBinding.fernRelative.setOnClickListener {
            afterClickTextView("蕨类植物门")
        }
        activityRecogHisAndColBinding.gymnospermRelative.setOnClickListener {
            afterClickTextView("裸子植物门")
        }
        activityRecogHisAndColBinding.angiospermRelative.setOnClickListener {
            afterClickTextView("被子植物门")
        }

    }

    //点击了上面六个RelativeLayout之后发生的变化
    private fun afterClickTextView(text:String){
        activityRecogHisAndColBinding.retrieveByDoorText.text=text //改变文字
        activityRecogHisAndColBinding.retrieveByDoorText.setTextColor(resources.getColor(R.color.RGB_76_219_154))
        activityRecogHisAndColBinding.retrieveByDoorImage.setImageResource(R.drawable.more_green) //让"更多"icon变绿
        activityRecogHisAndColBinding.retrieveByDoorLinear.visibility = View.GONE // 隐藏六个TextView的布局
        //判断当前Activity类型后发送请求
        sendRequest(text)
    }
    //判断当前Activity类型，决定发送识别历史请求还是我的收藏请求
    private fun sendRequest(text:String){
        Log.d(TAG, "RecogHisAndColActivity处的sendRequest方法，door值-->${text}")
        if (type==0){
            sp.getString("token","233")
                ?.let { mRecogHisAndColViewModel.getRecognitionHistory(it,text,pageNo,pageSize,plantsInfoCallback) }
        }else{
            sp.getString("token","233")
                ?.let { mRecogHisAndColViewModel.getCollectionPlants(it,text,pageNo,pageSize,plantsInfoCallback) }
        }
    }

}