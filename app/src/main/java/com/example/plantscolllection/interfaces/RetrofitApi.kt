package com.example.plantscolllection.interfaces

import com.example.plantscolllection.bean.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface RetrofitApi {
    //登录
    @POST("user/login")
    suspend fun login(@Body login: Login): BaseBean<String>
    //识别历史
    @GET("botany/history/{door}/{pageNo}/{pageSize}")
    suspend fun getRecognitionHistory(@Header("token") token:String, @Path("door") door:String, @Path("pageNo") pageNo:Int, @Path("pageSize") pageSize:Int):BaseBean<List<PlantsInfo>>
    //分页获取收藏的植物
    @GET("/botany/collect/{door}/{pageNo}/{pageSize}")
    suspend fun getCollectionPlants(@Header("token") token:String, @Path("door") door:String, @Path("pageNo") pageNo:Int, @Path("pageSize") pageSize:Int):BaseBean<List<PlantsInfo>>
    //根据id获取植物详情
    @GET("botany/get/{id}")
    suspend fun getPlantsDetails(@Path("id") id:Int):BaseBean<PlantsDetails>
    //收藏植物
    @POST("/botany/addcollect/{id}")
    suspend fun collectPlants(@Header("token") token:String, @Path("id") id:Int):BaseBean<String>
    //搜索植物
    @POST("/botany/search")
    suspend fun searchPlants(@Body requestParm: RequestParm):BaseBean<List<Botanysearch>>
    //获取热门搜索的植物
    @GET("/botany/hot")
    suspend fun getHotBotany():BaseBean<List<HotBotany>>
    //拍照页上传图片
    @Multipart
    @POST("/file/up")
    suspend fun postImage(@Part part:MultipartBody.Part):BaseBean<String>
    //获取识别后的植物对象BotanyRes（根据返回来的图片路径）
    @GET("/rec/getText")
    suspend fun getBotanyRes(@Header("token") token:String, @Query("path") filePath:String):BaseBean<List<BotanyRes>>
    //添加笔记
    @POST("/note/addvo")
    suspend fun addNote(@Header("token") token:String, @Body noteVo: NoteVo):BaseBean<Int>
    //获取单个笔记
    @GET("/note/selectOne/{id}")
    suspend fun getNote(@Header("token") token:String, @Path("id") noteId:Int):BaseBean<NoteVo>
    //搜索笔记
    @POST("/note/search")
    suspend fun searchNotes(@Body requestParm: RequestParm):BaseBean<List<NoteVo>>
    //删除笔记
    @POST("/note/removeVo")
    suspend fun deleteNotes(@Header("token") token:String, @Body noteVoidList: List<Int>):BaseBean<String>
    //补充笔记
    @POST("/note/addSupple/{noteId}")
    suspend fun supplementNote(@Body newNote: NewNote, @Path("noteId") noteId:Int):BaseBean<String>
    //分页展示笔记
    @GET("/note/listVo/{pageNo}/{pageSize}")
    suspend fun getNotes(@Header("token") token:String, @Path("pageNo") pageNo:Int, @Path("pageSize") pageSize:Int):BaseBean<List<NoteVo>>
    //获得用户信息
    @GET("/user/get")
    suspend fun getUserInfo(@Header("token") token:String):BaseBean<User>
    //更新用户信息
    @POST("/user/update")
    suspend fun updateUserInfo(@Header("token") token:String, @Body user: User):BaseBean<String>
    //文字扩展
    @GET("botany/name/{name}")
    suspend fun getTextExpandList(@Path("name") searchText: String):BaseBean<List<String>>
    //天气
    @GET("weather")
    suspend fun getWeather():BaseBean<List<Weather>>

}