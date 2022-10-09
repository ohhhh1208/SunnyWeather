package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

/**
 * Created by oxq on 2022/6/6.
 * ViewModel专门用于存放与界面相关的数据
 * 推荐的做法是，永远只暴露不可变的LiveData给外部。
 * 这样在非ViewModel中就只能观察LiveData的数据变化，而不能给LiveData设置数据
 */
class PlaceViewModel : ViewModel() {

    //MutableLiveData是一种可变的LiveData
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    //switchMap使用场景非常固定：如果ViewModel中的某个LiveData对象是调用另外的方法获取的
    //(可能每次返回的是一个新的LiveData对象,此时LiveData对象可变也变成不可变了,因为一直在观察旧的)，
    // 那么我们就可以借助switchMap()方法，将这个LiveData对象转换成另外一个可观察的LiveData对象
    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        //转换逻辑
        //将转换函数中返回的LiveData 对象转换成另一个可观察的LiveData 对象
        Repository.searchPlaces(query)
    }

    //ViewModel不能持有activity/Fragment的实例,ViewModel生命周期比他们长,
    //持有会引起activity/Fragment的内存泄漏
    fun searchPlaces(query: String) {
        //.value是setValue()的语法糖写法
        searchLiveData.value = query
    }

    /**
     * 仓库层中这几个接口的内部没有开启线程，因此也不必借助LiveData对象来观察数据变化，直接调用仓库层中相应的接口并返回
     */
    fun savePlace(place: Place) = Repository.savePlace(place)
    fun getSavedPlace() = Repository.getSavedPlace()
    fun isPlaceSaved() = Repository.isPlaceSaved()

}