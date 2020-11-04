package com.tans.androidhandlerdemo

abstract class Handler {
    val messageQueue: MessageQueue

    constructor() {
        messageQueue = Looper.tls.get()?.messageQueue ?: error("Looper is null!!")
    }

    constructor(looper: Looper) {
        messageQueue = looper.messageQueue
    }

    fun sendMessage(msg: Message) = messageQueue.enqueue(msg)

    fun obtainMessage(msg: Any): Message = Message(this, msg)

    abstract fun handleMessage(msg: Message)
}