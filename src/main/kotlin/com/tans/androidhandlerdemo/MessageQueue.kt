package com.tans.androidhandlerdemo

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

class MessageQueue {
    private var isQuite: Boolean = false
    val messages: Channel<Message> = Channel(Channel.UNLIMITED)
    fun next(): Message? = runBlocking {
        if (isQuite) {
            null
        } else {
            messages.receive()
        }
    }

    fun enqueue(newMessage: Message) = runBlocking {
        messages.send(newMessage)
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