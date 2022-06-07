package com.sunnyweather.android.logic.model

/**
 * Created by oxq on 2022/6/6.
 */
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)
