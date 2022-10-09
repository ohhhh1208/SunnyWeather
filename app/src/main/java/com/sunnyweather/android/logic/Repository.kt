package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * Created by oxq on 2022/6/6.
 * 仓库层的统一封装入口
 * 仓库层有点像是一个数据获取与缓存的中间层，在本地没有缓存数据的情况下就去网络层获取，
 * 如果本地已经有缓存了，就直接将缓存数据返回
 */
object Repository {

    /**
     * fire()按照liveData()函数的参数接收标准定义的一个高阶函数
     * 由于使用协程简化网络回调,导致每个网络请求接口都可能抛出异常
     * 所以在统一的入口函数中封装进行try catch 处理
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
    //为了能将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个LiveData 对象
    //liveData()函数自动构建并返回一个LiveData 对象，然后在它的代码块中提供一个挂起函数的上下文，
    //这样我们就可以在liveData()函数的代码块中调用任意的挂起函数
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    //将liveData()函数的线程参数类型指定成了Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中了。
    //Android 是不允许在主线程中进行网络请求的,诸如读写数据库之类的本地数据操作也是不建议在主线程中进行
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    /**
     * 统一封装刷新天气
     */
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        //coroutineScope函数也是一个挂起函数,能继承外部的协程的作用域并创建一个子协程
        //它可以保证其作用域内的所有代码和子协程在全部执行完之前，外部的协程会一直被挂起
        coroutineScope {
            //async函数必须在协程作用域当中才能调用，它会创建一个新的子协程并返回一个Deferred对象
            //要调用Deferred对象的await()方法即可要获取async函数代码块的执行结果
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(
                    realtimeResponse.result.realtime,
                    dailyResponse.result.daily
                )
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    /**
     * 实现方式并不标准，因为即使是对SharedPreferences文件进行读写的操作，也是不太建议在主线程中进行，虽然它的执行速度
    通常会很快。最佳的实现方式肯定还是开启一个线程来执行这些比较耗时的任务，然后通过LiveData对象进行数据返回
     */
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}
