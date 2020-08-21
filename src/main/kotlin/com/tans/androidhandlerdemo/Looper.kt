package com.tans.androidhandlerdemo

import java.lang.RuntimeException

class Looper(val messageQueue: MessageQueue) {

    fun loop() {
        while (true) {
            val msg = messageQueue.next() ?: return
            msg.target.handleMessage(msg)
        }
    }

    companion object {

        val tls: ThreadLocal<Looper> = ThreadLocal()

        private var mainLooper: Looper? = null

        fun prepare() {
            if (tls.get() == null) {
                tls.set(Looper(MessageQueue()))
            }
            println("Prepare CurrentThread: ${Thread.currentThread().name}")
        }

        fun loop() {
            val looper = tls.get() ?: throw RuntimeException("Looper is null.")
            looper.loop()
            println("Loop CurrentThread: ${Thread.currentThread().name}")
        }

        fun prepareMain() {
            if (Thread.currentThread().name != "main_thread") {
                error("Current thread is not main.")
            }
            if (tls.get() == null || mainLooper == null) {
                tls.set(Looper(MessageQueue()))
                mainLooper = tls.get()
            }
        }
        fun mainLooper(): Looper {
            return mainLooper ?: error("Main Looper is null..")
        }
    }
}