package com.example.plantscolllection.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivityAnnalsRetrieveBinding
import com.example.plantscolllection.utils.Utils


class AnnalsRetrieveActivity : AppCompatActivity() {

    private lateinit var activityAnnalsRetrieveBinding: ActivityAnnalsRetrieveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this, false)
        activityAnnalsRetrieveBinding = DataBindingUtil.setContentView(this, R.layout.activity_annals_retrieve)
        activityAnnalsRetrieveBinding.lifecycleOwner = this
        initView()
    }

    private fun initView() {
        //开始检索
        activityAnnalsRetrieveBinding.startRetrieveCard.setOnClickListener {
            if (TextUtils.isEmpty(activityAnnalsRetrieveBinding.annalsRetrievePageEdit.text.toString())) {
                Toast.makeText(this, "检索内容不能为空", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, RetrieveResultActivity::class.java)
                intent.putExtra(
                    "plants_retrieve",
                    activityAnnalsRetrieveBinding.annalsRetrievePageEdit.text.toString()
                )
                startActivity(intent)
            }
        }
        //返回键
        activityAnnalsRetrieveBinding.backImage.setOnClickListener {
            finish()
        }
    }


}