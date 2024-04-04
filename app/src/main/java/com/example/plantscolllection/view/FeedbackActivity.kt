package com.example.plantscolllection.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivityFeedbackBinding
import com.example.plantscolllection.utils.Utils

class FeedbackActivity : AppCompatActivity() {

    private lateinit var activityFeedbackBinding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityFeedbackBinding = DataBindingUtil.setContentView(this,R.layout.activity_feedback)
        activityFeedbackBinding.lifecycleOwner = this
        initView()
    }

    private fun initView() {
        //返回键
        activityFeedbackBinding.backImage.setOnClickListener {
            finish()
        }
        //提交反馈的CardView点击事件
        activityFeedbackBinding.submitFeedBackCardView.setOnClickListener {
            if (TextUtils.isEmpty(activityFeedbackBinding.feedBackPageEdit.text.toString())){
                Toast.makeText(this,"意见反馈不能为空！",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"意见反馈成功！",Toast.LENGTH_SHORT).show()
                activityFeedbackBinding.feedBackPageEdit.setText("")
            }
        }
        //意见反馈的EditText监听事件
        activityFeedbackBinding.feedBackPageEdit.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = activityFeedbackBinding.feedBackPageEdit.text.toString().length
                if(length>=200){
                    Toast.makeText(this@FeedbackActivity,"您已经达到最大输入字数",Toast.LENGTH_SHORT).show()
                    activityFeedbackBinding.feedBackPageText.setTextColor(Color.RED)
                }else{
                    activityFeedbackBinding.feedBackPageText.setTextColor(Color.BLACK)
                }
                activityFeedbackBinding.feedBackPageText.text="${length}/200"
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}