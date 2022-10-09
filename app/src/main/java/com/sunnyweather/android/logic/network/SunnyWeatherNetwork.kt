package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Created by oxq on 2022/6/6.
 * 统一的网络数据源访问入口，对所有网络请求的API进行封装
 */
object SunnyWeatherNetwork {
    //suspend用于暂停执行当前协程，并保存所有局部变量，
    //被标记为suspend的函数只能运行在协程或者其他suspend函数。
    //suspend关键字只能将一个函数声明成挂起函数，但无法给它提供协程作用域

    //使用ServiceCreator创建了一个PlaceService接口的动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()
    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    //发起搜索城市数据请求(挂起协程的执行等待异步计算的结果)
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather(lng, lat).await()

    /**
     *简化Retrofit回调的写法，借助协程技术来实现的，定义await()函数
     * 所有返回值是Call类型的Retrofit网络请求接口就都可以直接调用await()函数
     */
    private suspend fun <T> Call<T>.await(): T {
        // suspendCoroutine函数必须在协程作用域或挂起函数中才能调用，它接收一个Lambda表达式参数，
        // 主要作用是将当前协程立即挂起，然后在一个普通的线程中执行Lambda 表达式中的代码。
        // Lambda表达式的参数列表上会传入一个Continuation参数，
        // 调用它的resume()方法或resumeWithException()可以让协程恢复执行。
        return suspendCoroutine { continuation ->
            //调用enqueue()方法让Retrofit发起网络请求
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}