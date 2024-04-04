package com.example.plantscolllection.view

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.IdentificationResultAdapter
import com.example.plantscolllection.adapter.IdentificationResultFragmentAdapter
import com.example.plantscolllection.bean.BotanyRes
import com.example.plantscolllection.databinding.ActivityCameraBinding
import com.example.plantscolllection.interfaces.callback.IdentificationResultCallback
import com.example.plantscolllection.utils.MyBottomDialog
import com.example.plantscolllection.utils.RealPathFromUriUtils
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.CameraViewModel
import com.google.android.material.card.MaterialCardView
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.log

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var activityCameraBinding: ActivityCameraBinding
    private val noticeDialog by lazy { Utils.getNoticeDialog(this,R.style.NoticeDialogStyle,R.layout.notice_dialog,"图像识别中") }
    private lateinit var imageCapture: ImageCapture
    private val sp by lazy { getSharedPreferences("data", Context.MODE_PRIVATE) }
    private val cameraViewModel by viewModels<CameraViewModel>()
    private val BOTANY_ID_REQUEST_CODE = 1
    private val ALBUM_IMAGE_REQUEST_CODE = 2

    private val identificationResultCallback by lazy {
        object : IdentificationResultCallback {
            override fun getIdentificationResultSuccess(botanyResList: List<BotanyRes>) {
                noticeDialog.dismiss() //让弹窗消失
                activityCameraBinding.cropImageView.hideBackground() //隐藏遮罩层和裁剪框、网格线、矩形等
                Log.d(TAG, "CameraActivity处获得botanyResList-->${botanyResList}")
//                activityCameraBinding.cropImageView.setImageResource(R.drawable.plants_picture)
                loadBottomDialog(botanyResList) //BottomDialog初始化和它里面元素加载放到一个方法里
            }

            override fun getIdentificationResultFailed(errorMessage: String) {
            }

        }
    }

    companion object{
        const val TAG = "CameraActivity"
        private val fileBasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath+"/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.clear()
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityCameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        activityCameraBinding.lifecycleOwner=this
        //得到传过来的编号，判断是哪个页面跳转过来的
        val pageNo = intent.getIntExtra("page",-1)
        when(pageNo){
            0->{//从拍照图标过来
                fromCameraIcon()
            }
            1->{//从识别历史过来

            }
        }
        initOnClick()
        Log.d(TAG, "CameraActivity的onCreate方法被调用...")
    }

    private fun initOnClick() {
        //拍照按钮点击事件
        activityCameraBinding.takePhotoImage.setOnClickListener {
            val filePath = fileBasePath+System.currentTimeMillis()+".jpg"
            val file = File(filePath)
            Log.d(TAG, filePath)
            try {
                file.createNewFile()
            }catch (e: IOException){
                Log.d(TAG, "createFile error-->$e")
            }

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(error: ImageCaptureException)
                    {
                        // insert your code here.
                        Log.d(TAG, "保存图片出错onError->${error}")
                    }
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // insert your code here.
                        Log.d(TAG, "保存图片成功onImageSaved->${outputFileResults.savedUri}")
                        val decodeFile = BitmapFactory.decodeFile(filePath)
                        Log.d(TAG, "拍照得到的Bitmap的宽->${decodeFile.width};Bitmap的高->${decodeFile.height}")
                        val createScaledBitmap = Bitmap.createScaledBitmap(decodeFile, activityCameraBinding.cropImageView.width, activityCameraBinding.cropImageView.height, true)
                        Log.d(TAG, "cropImageView的宽->${activityCameraBinding.cropImageView.width};高->${activityCameraBinding.cropImageView.height}")
                        Log.d(TAG, "要给CropImageView设置的Bitmap的宽->${createScaledBitmap.width};Bitmap的高->${createScaledBitmap.height}")
                        activityCameraBinding.cropImageView.setImageBitmap(createScaledBitmap)
                        setViewVisibilityAfterTakingPhoto()
                        if (file.exists()){
                            file.delete()
                            Log.d(TAG, "文件已被删除")
                        }
                    }
                })
        }
        //确认裁剪按钮点击事件
        activityCameraBinding.confirmCropImage.setOnClickListener {
            noticeDialog.show() //让弹窗出现
            val file = bitmapToFile(activityCameraBinding.cropImageView.cropBitmap()) //将裁剪后的Bitmap转成File对象
//            postImage(file) //上传图片文件
        }
        //返回键点击事件
        activityCameraBinding.backImage.setOnClickListener {
            finish()
        }
        //相册点击事件
        activityCameraBinding.albumImage.setOnClickListener {
            val intent = Intent().apply {
                type="image/*"
                action=Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),ALBUM_IMAGE_REQUEST_CODE)
        }
    }
    //点击拍照按钮后一些View的Visibility属性改变
    private fun setViewVisibilityAfterTakingPhoto(){
        activityCameraBinding.previewView.visibility = View.INVISIBLE  //预览的previewView消失
        activityCameraBinding.gridView.visibility = View.INVISIBLE  //提供网格线效果的gridView消失
        activityCameraBinding.albumImage.visibility=View.INVISIBLE  //相册按钮消失
        activityCameraBinding.takePhotoImage.visibility=View.INVISIBLE  //拍照按钮消失
        activityCameraBinding.cropImageView.visibility=View.VISIBLE  //CropImageView出现
        activityCameraBinding.confirmCropImage.visibility=View.VISIBLE  //确认裁剪的按钮出现

    }

    //从拍照图标过来的逻辑
    private fun fromCameraIcon() {
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, deniedList ->
                if (allGranted) {//如果所有权限都同意了，就展示相机
//                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                    cameraProviderFuture = ProcessCameraProvider.getInstance(this)
                    cameraProviderFuture.addListener(Runnable {
                        val cameraProvider = cameraProviderFuture.get()
                        bindPreview(cameraProvider)
                    }, ContextCompat.getMainExecutor(this))
                } else {//如果有权限没同意，弹出提示，销毁Activity
//                    Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {


        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(activityCameraBinding.previewView.surfaceProvider)

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

//        Log.d(TAG, "JPEG quality-->${imageCapture.jpegQuality}")
//        Log.d(TAG, "Resolution infomation-->${imageCapture.resolutionInfo?.resolution?.width}*${imageCapture.resolutionInfo?.resolution?.height}")

        val camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture , preview)
    }

    //上传图片文件
    private fun postImage(file: File) {
        val requestFile = RequestBody.create(MediaType.parse("image/png"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        cameraViewModel.postImage(body,identificationResultCallback)
    }

    //将Bitmap类型的图片转化成file类型的方法，便于上传到服务器
    private fun bitmapToFile(bm:Bitmap):File {
        val filePath = fileBasePath+System.currentTimeMillis()+".jpg"   //文件路径
        val file = File(filePath) //文件路径对应的File对象
        val bos = BufferedOutputStream(FileOutputStream(file)) //通过file创建的缓冲字节输出流
        bm.compress(Bitmap.CompressFormat.PNG, 100, bos) //以最高画质输出成JPEG格式的文件
        bos.flush()
        bos.close()
        noticeDialog.dismiss() //让弹窗消失
        return file
    }

    //BottomDialog初始化和它里面元素加载的方法
    private fun loadBottomDialog(botanyResList: List<BotanyRes>){
        Log.d(TAG, "loadBottomDialog......................")
        val myBottomDialog = MyBottomDialog(this@CameraActivity,R.style.BottomDialog)
        myBottomDialog.setCanceledOnTouchOutside(false) //点击布局外不能收起myBottomDialog
        val bottomRelative = LayoutInflater.from(this@CameraActivity).inflate(R.layout.bottom_diaglog, null) as RelativeLayout //根布局对象
        myBottomDialog.setContentView(bottomRelative)
        myBottomDialog.show()
        //初始化ViewPager2的adapter
        val identificationResultFragmentAdapter = IdentificationResultFragmentAdapter(supportFragmentManager,lifecycle,botanyResList)
        //配置ViewPager2
        val viewPager2 = bottomRelative.findViewById<ViewPager2>(R.id.identification_result_view_pager).apply {
            adapter=identificationResultFragmentAdapter
        }
        //给"生成笔记"按钮添加点击事件
        bottomRelative.findViewById<MaterialCardView>(R.id.generate_note_card).apply {
            setOnClickListener {
                myBottomDialog.dismiss() //关闭myBottomDialog，不然回来的时候会崩
                //把植物的信息传过去NoteDetailsActivity
                val intent = Intent(this@CameraActivity,NoteDetailsActivity::class.java)
                intent.putExtra("activity_type",1) //代表点击了CameraActivity的"生成笔记"跳过去的
                intent.putExtra("botany_id",botanyResList[viewPager2.currentItem].botanyId) //根据当前ViewPager2在的位置从数组中取得BotanyRes，获得id
                startActivity(intent)
                viewPager2.removeAllViews()
            }
        }
        //给"查看详情"按钮添加点击事件
        bottomRelative.findViewById<MaterialCardView>(R.id.check_detail_card).apply {
            setOnClickListener {
                myBottomDialog.dismiss() //关闭myBottomDialog，不然回来的时候会崩
                val intent = Intent(this@CameraActivity,PlantsDetailsActivity::class.java)
                intent.putExtra("botany_id",botanyResList[viewPager2.currentItem].botanyId) //根据当前ViewPager2在的位置从数组中取得BotanyRes，获得id
                startActivityForResult(intent,BOTANY_ID_REQUEST_CODE)
                viewPager2.removeAllViews()
            }
        }
        myBottomDialog.setOnCancelListener {
            //这里放 下滑后 的逻辑（返回预览画面）
            Log.d(TAG, "bottomSheetDialog被取消了...")
            activityCameraBinding.gridView.visibility=View.VISIBLE  //显示预览画面的网格线
            activityCameraBinding.previewView.visibility=View.VISIBLE //显示预览画面
            activityCameraBinding.albumImage.visibility=View.VISIBLE  //相册按钮出现
            activityCameraBinding.takePhotoImage.visibility=View.VISIBLE  //拍照按钮出现
            activityCameraBinding.confirmCropImage.visibility=View.INVISIBLE  //确认裁剪的按钮消失
            activityCameraBinding.cropImageView.apply {
                visibility=View.INVISIBLE //隐藏cropImageView
                showBackground() //显示裁剪框等
                setVertexDefaultPosition() //四个顶点恢复默认位置
            }

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "CameraActivity的onResume方法被调用...")
    }

    override fun onDestroy() {
        Log.d(TAG, "CameraActivity的onDestroy方法被调用...")
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){//根据PlantsDetailsActivity返回的结果码看看是否需要收藏植物
            BOTANY_ID_REQUEST_CODE->{
                if (resultCode== RESULT_OK){
                    val botanyId = data?.getIntExtra("botany_id_return",-1)
                    Log.d(TAG, "CameraActivity处的onActivityResult方法获取植物id-->${botanyId}")
                    if (botanyId!=-1){
                        sp.getString("token","233")?.let {token->
                            botanyId?.let {
                                cameraViewModel.collectPlants(token, it)
                            }
                        }
                    }
                }
            }
            ALBUM_IMAGE_REQUEST_CODE->{
                if (resultCode== RESULT_OK && data!=null){
                    activityCameraBinding.cropImageView.setImageURI(data.data) //让背景显示用户选择的图片
                    activityCameraBinding.cropImageView.visibility=View.VISIBLE //让cropImageView显示
                    activityCameraBinding.confirmCropImage.visibility=View.VISIBLE //显示裁剪按钮
                    activityCameraBinding.previewView.visibility=View.INVISIBLE //隐藏预览画面
                    activityCameraBinding.takePhotoImage.visibility=View.INVISIBLE //隐藏拍照按钮
                    activityCameraBinding.albumImage.visibility=View.INVISIBLE //隐藏相册按钮
                }
            }
        }
    }
}