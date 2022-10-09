# SunnyWeather

#第一行代码(第三版)范例项目:
#MVVM架构
#使用Jetpack




#MVVM架构:
#
#                            UI控制层
#                               |
#                           ViewModel层
#                               |
#                             仓库层
#                           /       \
#                本地数据源(model)   网络数据源(model)
#                      /                   \
#                  持久化文件            Webservice
#



#Jetpack:

#ViewModel专门用于存放与界面相关的数据的,
#所有与界面相关的数据都应该放在ViewModel中,
#ViewModel中的数据在屏幕旋转时不会丢失

#Lifecycles轻松感知到Activity 的生命周期

#LiveData
#LiveData与ViewModel结合在一起使用,并在数据发生变化的时候通知给观察者
#LiveData 在内部使用了Lifecycles 组件来自我感知生命周期的变化，
#从而可以在Activity 销毁的时候及时释放引用，避免产生内存泄漏的问题。
#
#


#