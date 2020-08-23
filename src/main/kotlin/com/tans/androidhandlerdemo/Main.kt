package com.tans.androidhandlerdemo

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

val workExecutors = Executors.newFixedThreadPool(5)
val mainExecutors = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "main_thread").apply {
        isDaemon = false
        priority = Thread.NORM_PRIORITY
    }
}

class MainThreadHandler(private val activity: Activity) : Handler(Looper.mainLooper()) {

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

    fun sendFinishActivityEvent() {
        sendMessage(obtainMessage(ActivityLife.OnStop))
        sendMessage(obtainMessage(ActivityLife.OnDestroy))
    }

}

val mainActivity: Activity = object : Activity {

    override fun onCreate() {
        println("OnCreate!!!")
        val handler = object : Handler() {

            override fun handleMessage(msg: Message) {
                println("Main Handler Receive A New Message: ${msg.msg}, Thread: ${Thread.currentThread().name}")
            }

        }
        Thread {
            println("OnCreate Sleep Thread: ${Thread.currentThread().name}")
            // Lots of works.
            Thread.sleep(500)
            handler.sendMessage(handler.obtainMessage("Hello, World!!!"))
        }.start()
        Single.fromCallable {
            println("Single is working on Thread: ${Thread.currentThread().name}")
            Thread.sleep(2000)
            "Received a message from network: Hello World... "
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.main())
            .doOnSuccess {
                println("Single DownStream Working Thread: ${Thread.currentThread().name}, $it")
            }
            .subscribe()
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

fun main() {
    mainExecutors.execute {
        Looper.prepareMain()
        Looper.mainLooper().loop()
    }
    workExecutors.execute {
        Thread.sleep(200)
        val mainActivityHandler = MainThreadHandler(activity = mainActivity)
        mainActivityHandler.sendStartActivityEvent()
    }
}