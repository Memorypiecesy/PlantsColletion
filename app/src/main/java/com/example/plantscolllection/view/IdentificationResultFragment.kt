package com.example.plantscolllection.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.BotanyRes
import com.example.plantscolllection.bean.PlantsDetails
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.FragmentIdentificationResultBinding
import com.squareup.picasso.Picasso

class IdentificationResultFragment(private val botanyRes: BotanyRes,private val leftArrow:Boolean,private val rightArrow:Boolean) : Fragment() {

    private lateinit var fragmentIdentificationResultBinding:FragmentIdentificationResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentIdentificationResultBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_identification_result,container,false)
        return fragmentIdentificationResultBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentIdentificationResultBinding.botanyRes=botanyRes
        Picasso.get().load(RetrofitClient.BASE_URL+botanyRes.photo).into(fragmentIdentificationResultBinding.picture) //加载植物图片
        //根据Fragment位置选择是否隐藏左右箭头
        if (!leftArrow){
            fragmentIdentificationResultBinding.leftArrowImage.visibility=View.INVISIBLE
        }
        if (!rightArrow){
            fragmentIdentificationResultBinding.rightArrowImage.visibility=View.INVISIBLE
        }
    }

}