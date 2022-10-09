package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

/**
 * Created by oxq on 2022/6/2.
 * 定义数据模型
 */

data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(
    val name: String,
    val location: Location,
    //使用@SerializedName注解的方式，来让JSON字段和Kotlin字段之间建立 映射关系
    @SerializedName("formatted_address") val address: String
)

data class Location(val lng: String, val lat: String)
