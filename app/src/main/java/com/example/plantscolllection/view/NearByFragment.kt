package com.example.plantscolllection.view

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.FragmentNearByBinding
import com.example.plantscolllection.interfaces.listener.FragmentOnKeyListener
import com.permissionx.guolindev.PermissionX

class NearByFragment : Fragment(), FragmentOnKeyListener{

    private lateinit var fragmentNearByBinding: FragmentNearByBinding
    private lateinit var aMap: AMap
    private lateinit var lastMarker:Marker
    private lateinit var lastPolyLine:Polyline
    private var latitude = 0.0
    private var longitude = 0.0
    private var distance = 0F

    private val onMyLocationChangeListener = AMap.OnMyLocationChangeListener { location ->
        if (latitude==0.0&&longitude==0.0){
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(location.latitude, location.longitude),100F,0F,0F)))
        }
        latitude=location.latitude
        longitude=location.longitude
        Log.d(TAG, "OnMyLocationChangeListener处维度-->${location.latitude}; 经度-->${location.longitude}")
    }

    //点击Marker后的回调接口，回调函数的参数是点击的Marker
    private val markerClickListener = AMap.OnMarkerClickListener { marker->

        if (!::lastMarker.isInitialized){
            lastMarker=marker
            //画新的线
            lastPolyLine = aMap.addPolyline(PolylineOptions().setDottedLine(true).addAll(mutableListOf(
                LatLng(latitude,longitude),
                LatLng(marker.position.latitude,marker.position.longitude))).width(10F).color(resources.getColor(R.color.RGB_76_219_154)))
            //计算距离
            distance = AMapUtils.calculateLineDistance(LatLng(marker.position.latitude,marker.position.longitude),LatLng(latitude,longitude))
        }

        if (lastMarker!=marker){//如果这次点击的marker跟上次点击的不一样
            lastMarker.hideInfoWindow() //隐藏上次marker的InfoWindow
            lastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.point_not_selected)) //上次marker的图标变为没选中
            marker.showInfoWindow() //打开这次marker的InfoWindow
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.point_selected)) //这次marker的图标变为选中
            if (::lastPolyLine.isInitialized){
                lastPolyLine.remove()   //撤销上一次的线
            }
            //画新的线
            lastPolyLine = aMap.addPolyline(PolylineOptions().setDottedLine(true).addAll(mutableListOf(
                LatLng(latitude,longitude),
                LatLng(marker.position.latitude,marker.position.longitude))).width(10F).color(resources.getColor(R.color.RGB_76_219_154)))
            //计算距离
            distance = AMapUtils.calculateLineDistance(LatLng(marker.position.latitude,marker.position.longitude),LatLng(lastMarker.position.latitude,lastMarker.position.longitude))
            //更新上次点击的Marker为这次点击的Marker
            lastMarker=marker
            Log.d(TAG, "不相等。。。")
        }else{//如果这次点击的marker跟上次点击的一样
            //根据marker的InfoWindow状态选择打开/关闭
            if (marker.isInfoWindowShown){
                marker.hideInfoWindow()
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.point_not_selected)) //这次marker的图标变为没选中
            }else{
                marker.showInfoWindow()
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.point_selected)) //这次marker的图标变为选中
            }
            Log.d(TAG, "相等。。。")
        }
        Log.d(TAG, "上次的marker-->${lastMarker}")
        Log.d(TAG, "这次的marker-->${marker}")
        // 返回 true 则表示接口已响应事件，否则返回false
        true
    }
    //设置InfoWindow的适配器
    private val myInfoWindowAdapter = object :AMap.InfoWindowAdapter{
        override fun getInfoWindow(p0: Marker?): View? {
            val view = LayoutInflater.from(requireActivity()).inflate(R.layout.item_plants_in_map,null)
            val distanceTextView = view.findViewById<TextView>(R.id.distance_text).apply {
                text="${distance.toInt()}m"
            }
            return view
        }

        override fun getInfoContents(p0: Marker?): View? { return null }
    }

    companion object{
        const val TAG = "NearByFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        activity?.let {
            it.window.statusBarColor = Color.WHITE
        }
        fragmentNearByBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_near_by,container,false)
        return fragmentNearByBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission(savedInstanceState) //请求定位权限
        //EditText监听
        fragmentNearByBinding.nearbyPageEdit.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //搜索框中文字为空，隐藏清除图标；否则显示
                if (TextUtils.isEmpty(fragmentNearByBinding.nearbyPageEdit.text)){
                    fragmentNearByBinding.nearbyPageImageClear.visibility= View.INVISIBLE
                }else{
                    fragmentNearByBinding.nearbyPageImageClear.visibility= View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
        //清除图标点击事件
        fragmentNearByBinding.nearbyPageImageClear.setOnClickListener {
            fragmentNearByBinding.nearbyPageEdit.setText("") //清空输入框
        }
    }

    private fun requestPermission(savedInstanceState: Bundle?) {
        PermissionX.init(this)
            .permissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
            .request { allGranted, _, deniedList ->
                if (allGranted) {//如果所有权限都同意了，就展示相机
                    loadGaodeMap(savedInstanceState)
                } else {//如果有权限没同意，弹出提示，销毁Activity
//                    Toast.makeText(requireActivity(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }
            }
    }

    //显示定位蓝点
    private fun showLocationPoint(){
        val myLocationStyle = MyLocationStyle().apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE) //定位蓝点的模式
            myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_point)) //定位蓝点的icon
            interval(10000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            radiusFillColor(Color.TRANSPARENT) //精度圈的填充颜色
            strokeColor(Color.TRANSPARENT) //精度圈的边框颜色
        }
        aMap.apply {
            setMyLocationStyle(myLocationStyle) //设置样式
            uiSettings.apply {
                isMyLocationButtonEnabled=true //设置默认定位按钮是否显示
                isZoomControlsEnabled=false //缩放按钮
                isScaleControlsEnabled=true
            }

            isMyLocationEnabled=true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            setOnMyLocationChangeListener(onMyLocationChangeListener)
            setInfoWindowAdapter(myInfoWindowAdapter)
            setOnMarkerClickListener(markerClickListener)
        }
    }

    //添加点的方法
    private fun addMarker() {
        val markerOption1 = MarkerOptions().apply {
            position(LatLng(21.152102,110.300875))
            title("湛江")
            snippet("广东海洋大学")
            draggable(true)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.point_not_selected))
        }
        val markerOption2 = MarkerOptions().apply {
            position(LatLng(21.151740,110.301874))
            title("湛江")
            snippet("广东海洋大学")
            draggable(true)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.point_not_selected))
        }
        val markerOption3 = MarkerOptions().apply {
            position(LatLng(21.152402,110.301075))
            title("湛江")
            snippet("广东海洋大学")
            draggable(true)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.point_not_selected))
        }
        val markerOption4 = MarkerOptions().apply {
            position(LatLng(21.151502,110.301475))
            title("湛江")
            snippet("广东海洋大学")
            draggable(true)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.point_not_selected))
        }
        aMap.apply {
            addMarker(markerOption1)
            addMarker(markerOption2)
            addMarker(markerOption3)
            addMarker(markerOption4)
        }
    }

    //加载高德地图
    private fun loadGaodeMap(savedInstanceState: Bundle?) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        fragmentNearByBinding.mapView.onCreate(savedInstanceState)
        //初始化地图控制器对象
        if (!::aMap.isInitialized) {
            aMap = fragmentNearByBinding.mapView.map
        }
        MapsInitializer.updatePrivacyShow(requireActivity(),true,true)
        MapsInitializer.updatePrivacyAgree(requireActivity(),true)
        //显示定位蓝点
        showLocationPoint()
    }

    //按下Enter键的时候的事件
    override fun onKeyEnter() {
        if (TextUtils.isEmpty(fragmentNearByBinding.nearbyPageEdit.text)){
            Toast.makeText(requireActivity(),"搜索内容不能为空！",Toast.LENGTH_SHORT).show()
        }else{
            addMarker()
        }
    }


}