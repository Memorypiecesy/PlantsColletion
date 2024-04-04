package com.example.plantscolllection.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.User
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.ActivityInfoSettingBinding
import com.example.plantscolllection.interfaces.callback.UpdateUserInfoCallback
import com.example.plantscolllection.utils.RealPathFromUriUtils
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.InfoSettingViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class InfoSettingActivity : AppCompatActivity() {

    private lateinit var activityInfoSettingBinding: ActivityInfoSettingBinding
    private val noticeDialog by lazy { Utils.getNoticeDialog(this,R.style.NoticeDialogStyle,R.layout.notice_dialog,"更新信息中") }
    private val infoSettingViewModel by viewModels<InfoSettingViewModel>()
    private val AVATAR_REQUEST_CODE = 1
    private lateinit var imageUri:Uri
    private var imagePath:String? = null
    private lateinit var userThis: User

    private val updateUserInfoCallback by lazy {
        object : UpdateUserInfoCallback {
            override fun updateUserInfoSuccess(updateSuccess: String) {
                Log.d(TAG, "InfoSettingActivity处更新用户信息成功->${updateSuccess}")
                Toast.makeText(this@InfoSettingActivity,"用户信息更新成功",Toast.LENGTH_SHORT).show()
                noticeDialog.dismiss() //让"更新信息中"弹窗消失
            }

            override fun updateUserInfoFailed(errorMessage: String) {
                Log.d(TAG, "InfoSettingActivity处更新用户信息失败->${errorMessage}")
                Toast.makeText(this@InfoSettingActivity,"用户信息更新失败，请重试",Toast.LENGTH_SHORT).show()
                noticeDialog.dismiss() //让"更新信息中"弹窗消失
            }

        }
    }

    companion object {
        private const val TAG = "InfoSettingActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityInfoSettingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_info_setting)
        activityInfoSettingBinding.lifecycleOwner = this
        //得到MeFragment传过来的User对象
        val gson = Gson()
        userThis = gson.fromJson(intent.getStringExtra("user"), User::class.java)
        imagePath=userThis.photo
        activityInfoSettingBinding.user = userThis
        Picasso.get().load(RetrofitClient.BASE_URL + userThis.photo)
            .into(activityInfoSettingBinding.infoSettingPageAvatar) //更新头像
        initView()
    }

    private fun initView() {
        //完成设置CardView点击事件
        activityInfoSettingBinding.completeSettingCard.setOnClickListener {
            noticeDialog.show() //让"更新信息中"弹窗出现
            //要更新的User
            val user = User()
            user.name=activityInfoSettingBinding.userNameEdit.text.toString()
            user.self=activityInfoSettingBinding.userBriefEdit.text.toString()
            user.botanyCount=userThis.botanyCount
            user.noteVoCount=userThis.noteVoCount
            if(::imageUri.isInitialized){
                val file = File(RealPathFromUriUtils.getRealPathFromUri(this@InfoSettingActivity,imageUri))
                val requestFile = RequestBody.create(MediaType.parse("image/png"), file)
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                infoSettingViewModel.postImageAndUpdateUserInfo(body,user,updateUserInfoCallback)
            }else{
                user.photo=imagePath
                infoSettingViewModel.updateUserInfoWithOriginalAvatar(user,updateUserInfoCallback)
            }

        }
        //返回键
        activityInfoSettingBinding.backImage.setOnClickListener {
            finish()
        }
        //头像点击事件：跳转相册选择图片
        activityInfoSettingBinding.infoSettingPageAvatar.setOnClickListener {
            val intent = Intent().apply {
                type="image/*"
                action=Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),AVATAR_REQUEST_CODE)
        }
        //用户昵称editText监听事件
        activityInfoSettingBinding.userNameEdit.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = activityInfoSettingBinding.userNameEdit.text.toString().length
                if(length>=12){
                    Toast.makeText(this@InfoSettingActivity,"您已经达到最大昵称输入字数",Toast.LENGTH_SHORT).show()
                    activityInfoSettingBinding.userNameLengthText.setTextColor(Color.RED)
                }else{
                    activityInfoSettingBinding.userNameLengthText.setTextColor(resources.getColor(R.color.RGB_76_219_154))
                }
                activityInfoSettingBinding.userNameLengthText.text="${length}/12"
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        //用户简介editText监听事件
        activityInfoSettingBinding.userBriefEdit.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = activityInfoSettingBinding.userBriefEdit.text.toString().length
                if(length>=24){
                    Toast.makeText(this@InfoSettingActivity,"您已经达到最大个人简介输入字数",Toast.LENGTH_SHORT).show()
                    activityInfoSettingBinding.userBriefLengthEdit.setTextColor(Color.RED)
                }else{
                    activityInfoSettingBinding.userBriefLengthEdit.setTextColor(resources.getColor(R.color.RGB_76_219_154))
                }
                activityInfoSettingBinding.userBriefLengthEdit.text="${length}/24"
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            AVATAR_REQUEST_CODE->{
                if (resultCode== RESULT_OK && data!=null){
                    activityInfoSettingBinding.infoSettingPageAvatar.setImageURI(data.data) //更新头像为用户选择的头像
                    imageUri= data.data!! //保存Uri变量，发送更新用户信息请求的时候用到
                }
            }

        }
    }

}