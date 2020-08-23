package com.tans.androidhandlerdemo

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.TimeUnit

class AndroidSchedulers {
    internal class MainHandlerScheduler(private val async: Boolean) :
            Scheduler() {

        val mainHandler = object : Handler(Looper.mainLooper()) {
            override fun handleMessage(msg: Message) {
                (msg.msg as Runnable).run()
            }
        }

        override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            var run = run
            run = RxJavaPlugins.onSchedule(run)
            val scheduled = ScheduledRunnable(run)
            val message: Message = mainHandler.obtainMessage(scheduled)
            mainHandler.sendMessage(message)
            return scheduled
        }

        override fun createWorker(): Worker {
            return HandlerWorker(mainHandler, async)
        }

        private class HandlerWorker internal constructor(
                private val handler: Handler,
                private val async: Boolean
        ) :
                Worker() {

            @Volatile
            private var disposed = false

            override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                var run = run ?: throw NullPointerException("run == null")

                if (disposed) {
                    return Disposables.disposed()
                }
                run = RxJavaPlugins.onSchedule(run)
                val scheduled = ScheduledRunnable(run)
                val message: Message = handler.obtainMessage(scheduled)
                handler.sendMessage(message)

                // Re-check disposed state for removing in case we were racing a call to dispose().
                if (disposed) {
                    return Disposables.disposed()
                }
                return scheduled
            }

            override fun dispose() {
                disposed = true
            }

            override fun isDisposed(): Boolean {
                return disposed
            }

        }

        private class ScheduledRunnable internal constructor(
                private val delegate: Runnable
        ) :
                Runnable, Disposable {

            @Volatile
            private var disposed // Tracked solely for isDisposed().
                    = false

            override fun run() {
                try {
                    delegate.run()
                } catch (t: Throwable) {
                    RxJavaPlugins.onError(t)
                }
            }

            override fun dispose() {
                disposed = true
            }

            override fun isDisposed(): Boolean {
                return disposed
            }

        }

    }

    companion object {
        fun main(): Scheduler = MainHandlerScheduler(false)
    }

}