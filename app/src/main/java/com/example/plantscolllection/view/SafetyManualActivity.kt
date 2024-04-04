package com.example.plantscolllection.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivitySafetyManualBinding
import com.example.plantscolllection.utils.Utils

class SafetyManualActivity : AppCompatActivity() {

    private lateinit var activitySafetyManualBinding: ActivitySafetyManualBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activitySafetyManualBinding = DataBindingUtil.setContentView(this,R.layout.activity_safety_manual)
        activitySafetyManualBinding.lifecycleOwner = this
        initView()
    }

    private fun initView() {
        activitySafetyManualBinding.backImage.setOnClickListener {
            finish()
        }
    }


}