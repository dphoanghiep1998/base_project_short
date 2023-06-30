package com.neko.hiepdph.skibyditoiletvideocall.data

data class MessageModel(
    val contentSent: String,
    val contentReceived: String,
    var isSent: Boolean = true
)