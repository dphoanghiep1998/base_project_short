package com.neko.hiepdph.skibyditoiletvideocall.data.model

data class MessageModel(
    val contentSent: String,
    val contentReceived: String,
    var isSent: Boolean = true
)