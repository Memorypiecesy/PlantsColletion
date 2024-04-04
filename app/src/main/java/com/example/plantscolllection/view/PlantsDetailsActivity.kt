package com.example.plantscolllection.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.PlantsDetails
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.ActivityPlantsDetailBinding
import com.example.plantscolllection.interfaces.callback.PlantsDetailsCallback
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.PlantsDetailsViewModel
import com.squareup.picasso.Picasso

class PlantsDetailsActivity : AppCompatActivity() {

    private lateinit var activityPlantsDetailBinding: ActivityPlantsDetailBinding
    private val plansDetailsViewModel by viewModels<PlantsDetailsViewModel>()
    private var botanyId=-1 //植物的id

    private val plantsDetailsCallback by lazy {
        object : PlantsDetailsCallback {
            override fun getPlantsDetailsSuccess(plantsDetails: PlantsDetails) {
                Log.d(TAG, "PlantsDetailsActivity处获取植物详情成功->${plantsDetails}")
                setPlantsDetails(plantsDetails) //设置数据
            }

            override fun getPlantsDetailsFailed(errorMessage: String) {
                Log.d(TAG, "PlantsDetailsActivity处获取植物详情失败->${errorMessage}")
            }


        }
    }

    private val countDownTimer = object : CountDownTimer(3000,1000){
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            activityPlantsDetailBinding.cancelCollectAlreadyImage.visibility=View.INVISIBLE //让"已取消收藏"图标消失
            activityPlantsDetailBinding.collectAlreadyImage.visibility=View.INVISIBLE //让"已收藏"图标消失
        }

    }

    companion object{
        private const val TAG = "PlantsDetailsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        Log.d(TAG, "onCreate...")
        activityPlantsDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_plants_detail)
        activityPlantsDetailBinding.lifecycleOwner = this
        //得到传过来的植物id，发送请求获取植物详情
        botanyId = intent.getIntExtra("botany_id",-1)
        if (botanyId!=-1){
            plansDetailsViewModel.getPlantsDetails(botanyId,plantsDetailsCallback)
        }
        initView()
    }

    private fun initView() {
        activityPlantsDetailBinding.collectionImage.setOnClickListener { //设置收藏图标点击后动态切换
            activityPlantsDetailBinding.collectionImage.isSelected = !activityPlantsDetailBinding.collectionImage.isSelected
        }
        activityPlantsDetailBinding.collectionImage.setOnClickListener { //右上角的"收藏"图标点击事件：根据图标状态显示或隐藏"已收藏"/"已取消收藏"icon
            if (activityPlantsDetailBinding.collectionImage.isSelected){
                activityPlantsDetailBinding.collectionImage.isSelected=false
                showCancelCollectionAlreadyIcon()
            }else{
                activityPlantsDetailBinding.collectionImage.isSelected=true
                showCollectionAlreadyIcon()
            }
            countDownTimer.start()
        }
        activityPlantsDetailBinding.backImage.setOnClickListener { //返回键点击事件：把要收藏的植物id传给CameraActivity
            returnBotanyId()
            finish()
        }
    }

    //通过得到的植物详情对象设置页面的数据
    private fun setPlantsDetails(plantsDetails: PlantsDetails){
        activityPlantsDetailBinding.plantsChineseName.text=plantsDetails.name
        activityPlantsDetailBinding.plantsLatinName.text=plantsDetails.latinName
        Picasso.get().load(RetrofitClient.BASE_URL+plantsDetails.photo[0]).into(activityPlantsDetailBinding.image1)
        Picasso.get().load(RetrofitClient.BASE_URL+plantsDetails.photo[1]).into(activityPlantsDetailBinding.image2)
        Picasso.get().load(RetrofitClient.BASE_URL+plantsDetails.photo[2]).into(activityPlantsDetailBinding.image3)
        activityPlantsDetailBinding.doorText.text=plantsDetails.door
        activityPlantsDetailBinding.familyText.text=plantsDetails.family
        activityPlantsDetailBinding.classNameText.text=plantsDetails.className
        activityPlantsDetailBinding.categoryText.text=plantsDetails.category
        activityPlantsDetailBinding.eyeText.text=plantsDetails.eye
        activityPlantsDetailBinding.seedText.text=plantsDetails.seed
        activityPlantsDetailBinding.growEnvironmentText.text=plantsDetails.growingEnv
        activityPlantsDetailBinding.disRangeText.text=plantsDetails.disRange
        activityPlantsDetailBinding.chiefValueText.text=plantsDetails.chiefValue
        activityPlantsDetailBinding.morCharText.text=plantsDetails.morChar
        //根据植物种类相应地设置CardView和TextView
        if (plantsDetails.species==0){
            activityPlantsDetailBinding.speciesText.text =  "珍稀物种"
            activityPlantsDetailBinding.speciesCardView.setCardBackgroundColor(this.resources.getColor(R.color.RGB_76_219_154))
        }else if(plantsDetails.species==1){
            activityPlantsDetailBinding.speciesText.text = "入侵物种"
            activityPlantsDetailBinding.speciesCardView.setCardBackgroundColor(this.resources.getColor(R.color.RGB_255_102_102_100_percent))
        }else{
            activityPlantsDetailBinding.speciesCardView.visibility=View.INVISIBLE
        }
    }


    //对"已收藏"图标操作
    private fun showCollectionAlreadyIcon() {

        //让"已收藏"图标显示
        activityPlantsDetailBinding.collectAlreadyImage.visibility=View.VISIBLE

        //创建平移动画对象
        val startTranslateAnim = TranslateAnimation(0F, 0F, -activityPlantsDetailBinding.collectAlreadyImage.height*1.0F, 0F)
        startTranslateAnim.duration = 500

        //"已收藏"图标开始动画
        activityPlantsDetailBinding.collectAlreadyImage.startAnimation(startTranslateAnim);

        //让"已取消收藏"图标消失
        activityPlantsDetailBinding.cancelCollectAlreadyImage.visibility=View.INVISIBLE
    }

    //对"已取消收藏"图标操作
    private fun showCancelCollectionAlreadyIcon() {

        //让"已取消收藏"图标显示
        activityPlantsDetailBinding.cancelCollectAlreadyImage.visibility= View.VISIBLE

        //开启平移动画
        val endTranslateAnim = TranslateAnimation(0F, 0F, -activityPlantsDetailBinding.cancelCollectAlreadyImage.height*1.0F, 0F)
        endTranslateAnim.duration = 500

        //"已取消收藏"图标开始动画
        activityPlantsDetailBinding.cancelCollectAlreadyImage.startAnimation(endTranslateAnim);

        //让"已收藏"图标消失
        activityPlantsDetailBinding.collectAlreadyImage.visibility=View.INVISIBLE
    }
    //Activity销毁的时候，如果收藏图标是亮着的，就返回植物id告诉CameraActivity执行收藏操作
    override fun onDestroy() {
        Log.d(TAG, "onDestroy...")
        super.onDestroy()
    }

    override fun onBackPressed() {
        returnBotanyId()
        finish()
    }

    private fun returnBotanyId(){
        if (activityPlantsDetailBinding.collectionImage.isSelected){
            val intent = Intent()
            intent.putExtra("botany_id_return",botanyId)
            setResult(RESULT_OK,intent)
            Log.d(TAG, "PlantsDetailsActivity处的onDestroy方法返回的botanyId-->${botanyId}")
        }
    }
}