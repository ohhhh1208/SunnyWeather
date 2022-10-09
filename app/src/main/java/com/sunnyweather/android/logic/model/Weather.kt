package com.sunnyweather.android.logic.model

/**
 * Created by oxq on 2022/6/6.
 * 封装Realtime和Daily对象
 */
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)
