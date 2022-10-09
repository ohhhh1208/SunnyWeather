package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by oxq on 2022/6/2.
 * 用于访问彩云天气城市搜索API的Retrofit接口，
 */
interface PlaceService {

    //只有query这个参数是需要动态指定的，我们使用@Query注解的方式来进行实现，
    // 另外两个参数是不会变的，因此固定写在@GET注解中即可

    //searchPlaces()方法的返回值被声明成了Call<PlaceResponse>，
    //这样Retrofit就会将服务器返回的JSON数据自动解析成PlaceResponse对象
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}