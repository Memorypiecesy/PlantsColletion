package com.example.plantscolllection.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivityPlantsClassifyBinding
import com.example.plantscolllection.utils.Data
import com.example.plantscolllection.utils.Utils

class PlantsClassifyActivity : AppCompatActivity() {

    private lateinit var activityPlansClassifyBinding: ActivityPlantsClassifyBinding
    private val breadList by lazy {  mutableListOf(Data.getTag(0))  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activityPlansClassifyBinding = DataBindingUtil.setContentView(this,R.layout.activity_plants_classify)
        activityPlansClassifyBinding.lifecycleOwner = this
        initView()
    }

    private fun initView() {
        //返回键
        activityPlansClassifyBinding.backImage.setOnClickListener {
            finish()
        }
        //添加事务
        supportFragmentManager.commit {
            val classifyFragment = ClassifyFragment(breadList,0)
            setReorderingAllowed(true)
            add(R.id.fragment_contain_view,classifyFragment)
            addToBackStack(Data.getTag(0)) //给新添加的事务添加名字，后面点击BreadAdater的item的时候弹回到对应的Fragment
        }
    }

    //按下返回键的时候，如果当前存放事务的栈backStack只有一个事务，就直接结束掉Activity；否则弹出栈顶的事务
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount==1){
            finish()
        }else{
            supportFragmentManager.popBackStack()
        }
    }
}