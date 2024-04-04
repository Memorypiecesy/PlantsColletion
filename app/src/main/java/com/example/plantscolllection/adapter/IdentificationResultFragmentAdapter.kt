package com.example.plantscolllection.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.plantscolllection.bean.BotanyRes
import com.example.plantscolllection.view.IdentificationResultFragment

//第一个参数是管理该Fragment的管理者，若Fragment在Activity中则传入getSupportFragmentManager()，
//若Fragment在Fragment中则传入getChildFragmentManager
class IdentificationResultFragmentAdapter(fragmentManager:FragmentManager, lifecycle: Lifecycle, private val botanyResList: List<BotanyRes>): FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int {
        return botanyResList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->IdentificationResultFragment(botanyResList[position], leftArrow = false, rightArrow = true)
            botanyResList.size-1->IdentificationResultFragment(botanyResList[position], leftArrow = true, rightArrow = false)
            else->IdentificationResultFragment(botanyResList[position], leftArrow = true, rightArrow = true)
        }
    }
}