package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Created by oxq on 2022/6/2.
 */
class SunnyWeatherApplication : Application() {

    companion object {
        const val TOKEN = "填入你申请到的令牌值"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}