package com.tans.androidhandlerdemo

import java.util.concurrent.Executors

val mainActivity: Activity = object : Activity {

    override fun onCreate() {
        println("OnCreate!!!")
    }

    override fun onStart() {
        println("OnStart!!!")
    }

    override fun onResume() {
        println("OnResume!!!")
    }

    override fun onStop() {
        println("OnStop!!!")
    }

    override fun onDestroy() {
        println("OnDestroy!!!")
    }

}

val workExecutors = Executors.newFixedThreadPool(5)
val mainExecutors = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "main_thread").apply {
        isDaemon = false
        priority = Thread.NORM_PRIORITY
    }
}

class ActivityLifeHandler(private val activity: Activity) : Handler(Looper.mainLooper()) {

    enum class ActivityLife {
        OnCreate, OnStart, OnResume, OnStop, OnDestroy
    }

    override fun handleMessage(msg: Message) {
        when (msg.msg) {
            ActivityLife.OnCreate -> activity.onCreate()
            ActivityLife.OnStart -> activity.onStart()
            ActivityLife.OnResume -> activity.onResume()
            ActivityLife.OnStop -> activity.onStop()
            ActivityLife.OnDestroy -> activity.onDestroy()
            else -> {}
        }
    }

    fun sendStartActivityEvent() {
        sendMessage(obtainMessage(ActivityLife.OnCreate))
        sendMessage(obtainMessage(ActivityLife.OnStart))
        sendMessage(obtainMessage(ActivityLife.OnResume))
    }

}

fun main() {
    mainExecutors.execute {
        Looper.prepareMain()
        Looper.mainLooper().loop()
    }
    workExecutors.execute {
        Thread.sleep(100)
        val mainActivityHandler = ActivityLifeHandler(activity = mainActivity)
        mainActivityHandler.sendStartActivityEvent()
    }
}