package com.example.plantscolllection.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.User
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.FragmentMeBinding
import com.example.plantscolllection.interfaces.callback.UserInfoCallback
import com.example.plantscolllection.viewmodel.MeFragmentViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class MeFragment : Fragment() {

    private lateinit var fragmentMeBinding: FragmentMeBinding
    private lateinit var userForSettingActivity: User
    private val sp by lazy { requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE) }
    private val meFragmentViewModel by viewModels<MeFragmentViewModel>()
    private val userInfoCallback by lazy {
        object : UserInfoCallback {
            override fun getUserInfoSuccess(user: User) {//更新用户信息，头像没加载用默认头像占位
                Picasso.get().load(RetrofitClient.BASE_URL+user.photo).placeholder(R.drawable.default_avatar).into(fragmentMeBinding.mePageAvatar) //更新头像
                fragmentMeBinding.user=user
                userForSettingActivity=user
                Log.d(TAG, "MeFragment处获取User成功->${user}")
            }

            override fun getUserInfoFailed(errorMessage: String) {
                Log.d(TAG, "MeFragment处获取User失败->${errorMessage}")
            }
        }
    }

    companion object{
        private const val TAG = "MeFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp.getString("token","no this key")?.let {token->
            meFragmentViewModel.getUserInfo(token,userInfoCallback)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.let {
            it.window.statusBarColor = resources.getColor(R.color.RGB_0_0_0_30_percent)
        }
        fragmentMeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_me,container,false)
        return fragmentMeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //收藏历史RelativeLayout点击事件
        fragmentMeBinding.recognitionHistoryRelative.setOnClickListener {
            val intent = Intent(requireActivity(),RecogHisAndColActivity::class.java)
            intent.putExtra("activity_type",0) //跳转到的Activity作为"识别历史"Activity存在，传0
            startActivity(intent)
        }
        //我的收藏RelativeLayout点击事件
        fragmentMeBinding.myCollectionRelative.setOnClickListener {
            val intent = Intent(requireActivity(),RecogHisAndColActivity::class.java)
            intent.putExtra("activity_type",1) //跳转到的Activity作为"我的收藏"Activity存在，传1
            startActivity(intent)
        }
        //安全手册RelativeLayout点击事件
        fragmentMeBinding.safetyManualRelative.setOnClickListener { //跳转到安全手册页
            val intent = Intent(requireActivity(),SafetyManualActivity::class.java)
            startActivity(intent)
        }
        //意见反馈RelativeLayout点击事件
        fragmentMeBinding.feedBackRelative.setOnClickListener { //跳转到意见反馈页
            val intent = Intent(requireActivity(),FeedbackActivity::class.java)
            startActivity(intent)
        }
        //资料设置RelativeLayout点击事件
        fragmentMeBinding.infoSettingRelative.setOnClickListener { //跳转到设置页，把User对象传过去
            val intent = Intent(requireActivity(),InfoSettingActivity::class.java)
            //靠Gson把User转成字符串传过去
            val gson = Gson()
            intent.putExtra("user",gson.toJson(userForSettingActivity))
            startActivity(intent)
        }
        //关于我们RelativeLayout点击事件
        fragmentMeBinding.aboutUsRelative.setOnClickListener { //跳转到关于我们页
            val intent = Intent(requireActivity(),AboutUsActivity::class.java)
            startActivity(intent)
        }

        super.onViewCreated(view, savedInstanceState)
    }

}