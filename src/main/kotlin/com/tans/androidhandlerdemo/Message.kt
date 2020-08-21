package com.tans.androidhandlerdemo

class Message(val target: Handler, val msg: Any) {
    var next: Message? = null
}