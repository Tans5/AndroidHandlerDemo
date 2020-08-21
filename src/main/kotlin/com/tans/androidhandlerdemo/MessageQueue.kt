package com.tans.androidhandlerdemo

import java.util.concurrent.atomic.AtomicReference

class MessageQueue {
    private var isQuite: Boolean = false
    val messages: AtomicReference<List<Message>?> = AtomicReference()
    fun next(): Message? {
        while (true) {
            if (isQuite)
                return null
            // println("Result: ${messages.get()}")
            val messages = this.messages.get() ?: continue
            val message = messages.getOrNull(0) ?: continue
            val newMessages = messages - message
            this.messages.set(newMessages)
            return message
        }
    }

    fun enqueue(newMessage: Message) {
        val messages = this.messages.get()
        if (messages == null) {
            this.messages.set(listOf(newMessage))
        } else {
            this.messages.set(messages + newMessage)
        }
    }

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