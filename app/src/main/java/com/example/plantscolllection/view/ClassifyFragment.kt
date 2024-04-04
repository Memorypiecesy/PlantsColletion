package com.example.plantscolllection.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.ClassifyAdapter
import com.example.plantscolllection.databinding.FragmentClassifyBinding
import com.example.plantscolllection.adapter.ItemBreadAdapter
import com.example.plantscolllection.utils.Data
import com.example.plantscolllection.utils.WaveSideBar


class ClassifyFragment(private val breadList:MutableList<String>, private val position:Int) : Fragment() {

    private lateinit var fragmentClassifyBinding:FragmentClassifyBinding
    private val itemBreadAdapter by lazy { ItemBreadAdapter(breadList) }
    private val classifyAdapter by lazy { ClassifyAdapter(classifyList) }
    private val classifyList by lazy { Data.getData(position) }

    companion object{
        const val TAG = "ClassifyFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentClassifyBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_classify, container, false)
        return fragmentClassifyBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //对数据List的String按字母序升序排列
        classifyList.sortBy {classify ->
            classify.index
        }
        //创建面包屑的LinearLayoutManager：水平方向
        val linearLayoutManager = LinearLayoutManager(requireActivity()).apply {
            orientation=LinearLayoutManager.HORIZONTAL
            scrollToPositionWithOffset(breadList.size-1,0) //每次来到新的Fragment都让BreadAdapter滑到最后一项
        }
        //配置面包屑的RecyclerView
        fragmentClassifyBinding.breadRecycler.apply {
            layoutManager=linearLayoutManager
            adapter=itemBreadAdapter
        }
        //配置ClassifyRecycler
        fragmentClassifyBinding.classifyRecycler.apply {
            layoutManager=LinearLayoutManager(requireActivity())
            adapter=classifyAdapter
        }
        //给BreadAdapter设置监听回调接口：点击后会回退
        itemBreadAdapter.setOnItemClickListener(object : ItemBreadAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, breadText: String) {
                for (i in position until breadList.size-1){
                    breadList.removeLast()
                }
                itemBreadAdapter.notifyDataSetChanged()
                linearLayoutManager.scrollToPositionWithOffset(breadList.size-1,0)
                //0代表不将breadText标签的Fragment弹出， POP_BACK_STACK_INCLUSIVE代表连该Fragment一起弹出
                parentFragmentManager.popBackStack(Data.getTag(position), FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (position==0) { //如果点击的是第一项，就添加一个Fragment
                    //添加事务
                    parentFragmentManager.commit {
                        val classifyFragment = ClassifyFragment(mutableListOf(Data.getTag(0)),0)
                        setReorderingAllowed(true)
                        add(R.id.fragment_contain_view,classifyFragment)
                        addToBackStack(Data.getTag(0)) //给新添加的事务添加名字，后面点击BreadAdater的item的时候弹回到对应的Fragment
                    }
                }

            }
        })
        //给ClassifyAdapter设置监听回调接口
        classifyAdapter.setOnItemClickListener(object :ClassifyAdapter.OnItemClickListener{
            override fun onItemClick(nameText: String) {
                if (position<5){ //position到5，说明到了"种"，最后一级了，不能再往下走了
                    val list = mutableListOf<String>().apply { //配置给下一个Fragment面包屑RecyclerView的数据List
                        addAll(breadList) //把当前Fragment的数据List加到新的List上去
                        removeLast() //删除最后一项"X类选择"文字
                        add(nameText) //添加点中的item的nameText
                        add(Data.getTag(position+1)) //添加一项"X类选择"文字
                    }
                    Log.d(TAG, "${position}位置处的list-->${list}")
                    //添加新一项事务
                    parentFragmentManager.commit {
                        val classifyFragment = ClassifyFragment(list,position+1)
                        setReorderingAllowed(true)
                        //先设置动画,再replace
                        setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                        replace(R.id.fragment_contain_view,classifyFragment)
                        addToBackStack(Data.getTag(position)) //给新添加的事务添加名字，后面点击BreadAdater的item的时候弹回到对应的Fragment
                    }
                }
            }
        })
        //sideBar监听事件：点击索引后滑到第一个具有该索引的item
        fragmentClassifyBinding.sideBar.setOnSelectIndexItemListener(WaveSideBar.OnSelectIndexItemListener { index ->
            for (i in classifyList.indices) {
                if (classifyList[i].index?.substring(0,1) == index) {
                    (fragmentClassifyBinding.classifyRecycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(i, 0)
                    return@OnSelectIndexItemListener
                }
            }
        })
    }


}