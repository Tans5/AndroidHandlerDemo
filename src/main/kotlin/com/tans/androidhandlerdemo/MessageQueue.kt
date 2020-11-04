package com.tans.androidhandlerdemo

import java.util.concurrent.LinkedBlockingQueue

class MessageQueue {
    private var isQuite: Boolean = false
    val messages: LinkedBlockingQueue<Message> = LinkedBlockingQueue()

    fun next(): Message? {
        return if (isQuite) {
            null
        } else {
            try {
                messages.take()
            } catch (e: InterruptedException) {
                isQuite = true
                null
            }
        }
    }

    fun enqueue(newMessage: Message) = messages.offer(newMessage)

    fun quite() {
        synchronized(this) {
            if (isQuite) {
                return
            } else {
                isQuite = true
            }
        }
    }

}