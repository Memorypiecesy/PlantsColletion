package com.example.plantscolllection.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.NewNote
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.bean.PlantsDetails
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.ActivityNoteDetailsBinding
import com.example.plantscolllection.interfaces.callback.NoteCallbackContract
import com.example.plantscolllection.interfaces.callback.PlantsDetailsCallback
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.NoteDetailsViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailsActivity : AppCompatActivity() {

    private lateinit var activityNoteDetailsBinding: ActivityNoteDetailsBinding
    private val PLANTS_CHOOSE_REQUEST_CODE = 1 //相册选择植物图片的请求码
    private val ADD_PICTURE_REQUEST_CODE = 2 //添加补充笔记中植物图片的请求码
    private val supplementNoteDialog by lazy { Utils.getNoticeDialog(this,R.style.NoticeDialogStyle,R.layout.notice_dialog,"补充笔记中") }
    private val saveNoteDialog by lazy { Utils.getNoticeDialog(this,R.style.NoticeDialogStyle,R.layout.notice_dialog,"保存笔记中") }
    private lateinit var aMapLocationClient: AMapLocationClient //声明AMapLocationClient类对象
    private lateinit var aMapLocationClientOption: AMapLocationClientOption //声明AMapLocationClientOption对象
    private val sp by lazy { getSharedPreferences("data", Context.MODE_PRIVATE) }
    private val noteDetailsViewModel by viewModels<NoteDetailsViewModel>()
    private var saveButtonClickTimes = 0 //保存按钮点击的次数
    private var noteIdThis:Int = -1 //上面的笔记的id，用来补充笔记时作为参数传递
    private var activityType = -1 //代表从哪个Activity过来的变量
    private var photoString = "" //获得的植物详情的图片地址

    //"目前处于可编辑状态"图标动画消失倒计时
    private val countDownTimer = object : CountDownTimer(3000,1000){
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            hideEditableStateIcon()
        }

    }
    //获得植物详情
    private val plantsDetailsCallback by lazy {
        object : PlantsDetailsCallback {
            override fun getPlantsDetailsSuccess(plantsDetails: PlantsDetails) {
                Log.d(TAG, "PlantsDetailsActivity处获取植物详情成功->${plantsDetails}")
                activityNoteDetailsBinding.plantsDetails=plantsDetails
                //观测时间TextView
                activityNoteDetailsBinding.observationTimeText.text = SimpleDateFormat("yyyy.MM.dd HH:mm").format(Date())
                //根据植物的物种类型来设置CardView背景颜色和TextView文字
                if (plantsDetails.species==0){
                    activityNoteDetailsBinding.speciesText.text =  "珍稀物种"
                    activityNoteDetailsBinding.speciesCardView.setCardBackgroundColor(resources.getColor(R.color.RGB_76_219_154))
                    Log.d(TAG, "NoteDetailsActivity处getPlantsDetailsSuccess的物种种类为-->${plantsDetails.species}")
                }else if (plantsDetails.species==1){
                    activityNoteDetailsBinding.speciesText.text =  "入侵物种"
                    activityNoteDetailsBinding.speciesCardView.setCardBackgroundColor(resources.getColor(R.color.RGB_255_102_102_100_percent))
                    Log.d(TAG, "NoteDetailsActivity处getPlantsDetailsSuccess的物种种类为-->${plantsDetails.species}")
                }else{
                    activityNoteDetailsBinding.speciesCardView.visibility=View.INVISIBLE
                }
                Picasso.get().load(RetrofitClient.BASE_URL+plantsDetails.photo[0]).into(activityNoteDetailsBinding.plantsImage) //加载植物图片
                Log.d(TAG, "NoteDetailsActivity处getPlantsDetailsSuccess处植物图片URL->${RetrofitClient.BASE_URL+plantsDetails.photo[0]}")
                photoString=plantsDetails.photo[0]
            }

            override fun getPlantsDetailsFailed(errorMessage: String) {
                Log.d(TAG, "NoteDetailsActivity处getPlantsDetailsFailed获取植物详情失败->${errorMessage}")
            }
        }
    }

    //添加笔记回调接口
    private val addNoteCallback by lazy {
        object : NoteCallbackContract.AddNoteCallback {

            override fun addNoteSuccess(noteId: Int) {
                Log.d(TAG, "NoteDetailsActivity处addNoteSuccess方法添加笔记成功->${noteId}")
                activityNoteDetailsBinding.touchToSupplementCard.visibility=View.VISIBLE //让"点击补充笔记"的CardView显示
                setViewDisabled() //让EditText不能再被编辑
                noteIdThis=noteId //保存笔记id
                saveNoteDialog.dismiss() //让"保存笔记中"的弹窗消失
                Toast.makeText(this@NoteDetailsActivity,"保存笔记成功", Toast.LENGTH_SHORT).show() //弹出Toast提示保存成功
            }

            override fun addNoteFailed(errorMessage: String) {
                Log.d(TAG, "NoteDetailsActivity处addNoteFailed方法添加笔记失败->${errorMessage}")
                saveNoteDialog.dismiss() //让"保存笔记中"的弹窗消失
                Toast.makeText(this@NoteDetailsActivity,"保存笔记失败", Toast.LENGTH_SHORT).show() //弹出Toast提示保存失败
            }
        }
    }

    //补充笔记回调接口
    private val supplementNoteCallback by lazy {
        object : NoteCallbackContract.SupplementNoteCallback {

            override fun supplementNoteSuccess(success: String) {
                Log.d(TAG, "NoteDetailsActivity处supplementNoteSuccess方法补充笔记成功->${success}")
                supplementNoteDialog.dismiss() //让"补充笔记中"的弹窗消失
                Toast.makeText(this@NoteDetailsActivity,"补充笔记成功", Toast.LENGTH_SHORT).show() //弹出Toast提示补充成功
            }

            override fun supplementNoteFailed(errorMessage: String) {
                Log.d(TAG, "NoteDetailsActivity处supplementNoteFailed方法补充笔记失败->${errorMessage}")
                supplementNoteDialog.dismiss() //让"补充笔记中"的弹窗消失
                Toast.makeText(this@NoteDetailsActivity,"补充笔记失败", Toast.LENGTH_SHORT).show() //弹出Toast提示补充失败
            }
        }
    }

    //获得单个笔记回调接口
    private val getNoteCallback by lazy {
        object : NoteCallbackContract.NoteCallback {

            override fun getNoteSuccess(noteVo: NoteVo) {
                Log.d(TAG, "NoteDetailsActivity的getNoteSuccess处获得单个笔记成功->${noteVo}")
                //保存笔记id
                noteIdThis=noteVo.id
                //把内容设置给页面
                activityNoteDetailsBinding.plantsChineseName.setText(noteVo.name)
                activityNoteDetailsBinding.plantsLatinName.setText(noteVo.latinName)
                activityNoteDetailsBinding.alias.setText(noteVo.alias)
                activityNoteDetailsBinding.doorEdit.setText(noteVo.door)
                activityNoteDetailsBinding.familyEdit.setText(noteVo.family)
                activityNoteDetailsBinding.classNameEdit.setText(noteVo.className)
                activityNoteDetailsBinding.categoryEdit.setText(noteVo.category)
                activityNoteDetailsBinding.eyeEdit.setText(noteVo.eye)
                activityNoteDetailsBinding.seedEdit.setText(noteVo.seed)
                activityNoteDetailsBinding.supplementContentEdit.setText(noteVo.text)
                activityNoteDetailsBinding.supplementTitleEdit.setText(noteVo.title)
                activityNoteDetailsBinding.observationTimeText.text = noteVo.time
                activityNoteDetailsBinding.observationPlaceText.text = noteVo.place
                activityNoteDetailsBinding.speciesText.text=if(noteVo.species==0) "珍稀物种"  else "入侵物种"
                Picasso.get().load(RetrofitClient.BASE_URL+noteVo.photo).into(activityNoteDetailsBinding.plantsImage) //加载植物图片
                //根据植物的物种类型来设置CardView背景颜色
                if (noteVo.species==0){
                    activityNoteDetailsBinding.speciesCardView.setCardBackgroundColor(resources.getColor(R.color.RGB_76_219_154))
                }else if(noteVo.species==1){
                    activityNoteDetailsBinding.speciesCardView.setCardBackgroundColor(resources.getColor(R.color.RGB_255_102_102_100_percent))
                }else{
                    activityNoteDetailsBinding.speciesCardView.visibility=View.INVISIBLE
                }
                //让EditText不可点击
                setViewDisabled()

            }

            override fun getNoteFailed(errorMessage: String) {
                Log.d(TAG, "NoteDetailsActivity的getNoteFailed处获得单个笔记失败->${errorMessage}")
            }
        }
    }

    private val aMapLocationListener = AMapLocationListener { aMapLocation ->
        if (aMapLocation != null) {
            if (aMapLocation.errorCode == 0) {
                //解析aMapLocation获取各个位置信息
                Log.d(TAG, "${aMapLocation.country},${aMapLocation.province},${aMapLocation.city},${aMapLocation.district},${aMapLocation.street},${aMapLocation.streetNum},")
                //设置给观测地点TextView
                activityNoteDetailsBinding.observationPlaceText.text = "${aMapLocation.province}${aMapLocation.city}${aMapLocation.district}${aMapLocation.street}${aMapLocation.streetNum}"
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.d(TAG, "location Error, ErrCode:${aMapLocation.errorCode}, errInfo:${aMapLocation.errorInfo}")
            }
        }
    }

    companion object{
        const val TAG = "NoteDetailsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityNoteDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_note_details)
        activityNoteDetailsBinding.lifecycleOwner = this


        //看看是从CameraActivity还是从MyNoteFragment跳过来的，执行不同逻辑
        activityType = intent.getIntExtra("activity_type", -1)
        Log.d(TAG, "跳过来的活动类型-->${activityType}")
        if (activityType==1){//如果从CameraActivity过来
            //得到CameraActivity传过来的植物id，发送请求获得PlantsDetails，在回调中把笔记内容填充
            val botanyId = intent.getIntExtra("botany_id",-1)
            Log.d(TAG, "botanyId-->${botanyId}")
            if (botanyId!=-1){
                noteDetailsViewModel.getPlantsDetails(botanyId,plantsDetailsCallback)
            }
            showEditableStateIcon() //让"目前处于可编辑状态"图标动画显示
            countDownTimer.start() //开始倒计时
        }else{//如果从MyNoteFragment和HomePageFragment过来
            //得到noteId，发送请求获取笔记内容
            val noteId = intent.getIntExtra("note_id",-1)
            Log.d(TAG, "noteId-->${noteId}")
            sp.getString("token","233")?.let {token->
                noteDetailsViewModel.getNote(token,noteId,getNoteCallback)
                Log.d(TAG, "进来请求getNote方法....")
            }
            saveButtonClickTimes = 1 //更新保存按钮点击次数的变量，不能再修改笔记，只能补充笔记。
            activityNoteDetailsBinding.saveText.text="编辑"
            activityNoteDetailsBinding.touchToSupplementCard.visibility=View.INVISIBLE //让"点击补充笔记"的CardView消失
        }
        initView()
        getCurPositionInfo() //通过高德地图获取当前位置信息
    }
    //通过高德地图获取当前位置信息
    private fun getCurPositionInfo() {
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)
        //初始化定位，要捕获异常信息，因为要进行合规检查，保证隐私政策合规
        try {
            aMapLocationClient = AMapLocationClient(applicationContext)
        } catch (e: Exception) {
            Log.d(TAG, "初始化AMapLocationClient失败-->${e}")
        }
        //设置定位回调监听
        aMapLocationClient.setLocationListener(aMapLocationListener)
        //初始化AMapLocationClientOption对象
        aMapLocationClientOption = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //设置高精度定位模式
            isOnceLocation = true //获取一次定位结果
            interval=5000
        }
        //给AMapLocationClient对象设置定位参数
        aMapLocationClient.setLocationOption(aMapLocationClientOption)
        //启动定位
        aMapLocationClient.startLocation()
    }

    private fun initView() {
        //"保存"文字
        activityNoteDetailsBinding.saveText.setOnClickListener {
            if (activityNoteDetailsBinding.saveText.text=="编辑"){
                activityNoteDetailsBinding.touchToSupplementCard.visibility=View.VISIBLE
                activityNoteDetailsBinding.saveText.text="保存"
            }else{
                if (saveButtonClickTimes==0){   //如果保存按钮第一次按，存入NoteVo
                    saveNoteDialog.show() //让"保存笔记中"的弹窗出现
                    val noteVo = NoteVo().apply {
                        //把内容设置给页面
                        title=activityNoteDetailsBinding.noteTitleEdit.text.toString()
                        name=activityNoteDetailsBinding.plantsChineseName.text.toString()
                        latinName=activityNoteDetailsBinding.plantsLatinName.text.toString()
                        alias=activityNoteDetailsBinding.alias.text.toString()
                        door=activityNoteDetailsBinding.doorEdit.text.toString()
                        family=activityNoteDetailsBinding.familyEdit.text.toString()
                        className=activityNoteDetailsBinding.classNameEdit.text.toString()
                        category=activityNoteDetailsBinding.categoryEdit.text.toString()
                        eye=activityNoteDetailsBinding.eyeEdit.text.toString()
                        seed=activityNoteDetailsBinding.seedEdit.text.toString()
                        time=activityNoteDetailsBinding.observationTimeText.text.toString()
                        place=activityNoteDetailsBinding.observationPlaceText.text.toString()
                        photo=photoString
                        species=if(activityNoteDetailsBinding.speciesText.text=="珍稀物种")  0 else 1
                    }
                    sp.getString("token","233")?.let {token->
                        noteDetailsViewModel.addNote(token, noteVo,addNoteCallback)
                    }

                }else{
                    activityNoteDetailsBinding.saveAndSupplementNoteText.text="补充笔记中"
                    supplementNoteDialog.show() //让"补充笔记中"的弹窗出现
                    val newNote = NewNote().apply {
                        title=activityNoteDetailsBinding.supplementTitleEdit.text.toString()
                        `val`=activityNoteDetailsBinding.supplementContentEdit.text.toString()
                    }
                    noteDetailsViewModel.supplementNote(newNote,noteIdThis,supplementNoteCallback)
                }
            }


        }
        //物种类型
        activityNoteDetailsBinding.speciesCardView.setOnClickListener{
            //点击一下就变一下物种类型CardView
            if (activityNoteDetailsBinding.speciesText.text=="珍稀物种"){
                activityNoteDetailsBinding.speciesCardView.setCardBackgroundColor(resources.getColor(R.color.RGB_255_102_102_100_percent))
                activityNoteDetailsBinding.speciesText.text="入侵物种"
                Log.d(TAG, "NoteDetailsActivity处右上角的CardView文字为珍稀物种时被点击")
            }else{
                activityNoteDetailsBinding.speciesCardView.setCardBackgroundColor(resources.getColor(R.color.RGB_76_219_154))
                activityNoteDetailsBinding.speciesText.text="珍稀物种"
                Log.d(TAG, "NoteDetailsActivity处右上角的CardView文字为入侵物种时被点击")
            }
        }
        //植物图片
        activityNoteDetailsBinding.plantsImage.setOnClickListener {
            val intent = Intent().apply {
                type="image/*"
                action=Intent.ACTION_GET_CONTENT }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),PLANTS_CHOOSE_REQUEST_CODE)
        }
        //点击补充笔记的CardView点击事件
        activityNoteDetailsBinding.touchToSupplementCard.setOnClickListener {
            //如果补充笔记的布局不可见，就动画显示
            if (activityNoteDetailsBinding.supplementTitleRelative.visibility==View.INVISIBLE){
                supplementNoteLayoutFadeIn()
                Log.d(TAG, "布局淡入")
            }
        }
        //添加图片的icon
        activityNoteDetailsBinding.addImage.setOnClickListener {
            val intent = Intent().apply {
                type="image/*"
                action=Intent.ACTION_GET_CONTENT }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),ADD_PICTURE_REQUEST_CODE)
        }
        //关闭补充笔记布局的icon
        activityNoteDetailsBinding.closeSupplementNoteImage.setOnClickListener {
            supplementNoteLayoutFadeOut()
            Log.d(TAG, "布局淡出")
        }
        //返回键
        activityNoteDetailsBinding.backImage.setOnClickListener {
            finish()
        }
    }
    //让EditText不能再被编辑，让ImageView植物图片不能被修改
    private fun setViewDisabled() {
        activityNoteDetailsBinding.noteTitleEdit.isEnabled=false
        activityNoteDetailsBinding.plantsChineseName.isEnabled=false
        activityNoteDetailsBinding.plantsLatinName.isEnabled=false
        activityNoteDetailsBinding.alias.isEnabled=false
        activityNoteDetailsBinding.doorEdit.isEnabled=false
        activityNoteDetailsBinding.familyEdit.isEnabled=false
        activityNoteDetailsBinding.classNameEdit.isEnabled=false
        activityNoteDetailsBinding.categoryEdit.isEnabled=false
        activityNoteDetailsBinding.eyeEdit.isEnabled=false
        activityNoteDetailsBinding.seedEdit.isEnabled=false
        activityNoteDetailsBinding.observationTimeText.isEnabled=false
        activityNoteDetailsBinding.observationPlaceText.isEnabled=false
        activityNoteDetailsBinding.speciesCardView.isEnabled=false
        activityNoteDetailsBinding.plantsImage.isEnabled=false
    }

    //让"目前处于可编辑状态"图标动画显示
    private fun showEditableStateIcon() {

        //让"目前处于可编辑状态"图标显示
        activityNoteDetailsBinding.editableStateImage.visibility= View.VISIBLE

        //创建平移显示动画对象
        val startTranslateAnim = TranslateAnimation(0F, 0F, -activityNoteDetailsBinding.editableStateImage.height*1.0F, 0F)
        startTranslateAnim.duration = 500

        //让"目前处于可编辑状态"图标开始动画
        activityNoteDetailsBinding.editableStateImage.startAnimation(startTranslateAnim);

    }

    //让"补充笔记"布局动画消失
    private fun hideEditableStateIcon() {

        //创建平移消失动画对象
        val endTranslateAnim = TranslateAnimation(0F, 0F, 0F, -activityNoteDetailsBinding.editableStateImage.height*1.0F)
        endTranslateAnim.duration = 500

        //让"目前处于可编辑状态"图标开始动画
        activityNoteDetailsBinding.editableStateImage.startAnimation(endTranslateAnim);


        endTranslateAnim.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) { //动画结束时让图标消失
                activityNoteDetailsBinding.editableStateImage.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })


    }

    //让"补充笔记"布局动画淡入
    private fun supplementNoteLayoutFadeIn() {

        val fadeInAnimation = AnimationUtils.loadAnimation(applicationContext,R.anim.fade_in) //创建动画对象
        activityNoteDetailsBinding.supplementTitleRelative.visibility=View.VISIBLE //让布局显示
        activityNoteDetailsBinding.supplementTitleRelative.startAnimation(fadeInAnimation) //开启淡入动画

    }

    //让"补充笔记"布局动画淡出
    private fun supplementNoteLayoutFadeOut() {

        val fadeOutAnimation = AnimationUtils.loadAnimation(applicationContext,R.anim.fade_out) //创建动画对象
        activityNoteDetailsBinding.supplementTitleRelative.startAnimation(fadeOutAnimation) //开启淡出动画
        fadeOutAnimation.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                activityNoteDetailsBinding.supplementTitleRelative.visibility=View.INVISIBLE //淡出动画结束时让布局消失
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){//根据PlantsDetailsActivity返回的结果码看看是否需要收藏植物
            PLANTS_CHOOSE_REQUEST_CODE->{
                if (resultCode== RESULT_OK && data!=null){
                    activityNoteDetailsBinding.plantsImage.setImageURI(data.data) //
                }
            }
            ADD_PICTURE_REQUEST_CODE->{
                if (resultCode== RESULT_OK && data!=null){
                    activityNoteDetailsBinding.addImage.setImageURI(data.data) //补充笔记的ImageView处设置用户给的图片
                    activityNoteDetailsBinding.supplementTitleRelative.visibility=View.VISIBLE //让补充笔记的布局显示
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //销毁定位客户端，同时销毁本地定位服务。
        aMapLocationClient.onDestroy()
    }

}