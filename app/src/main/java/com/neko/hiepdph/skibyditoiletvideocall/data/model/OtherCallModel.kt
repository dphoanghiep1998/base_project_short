package com.neko.hiepdph.skibyditoiletvideocall.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OtherCallModel(
    val image: Int,
    val circleImage: Int,
    val name: String,
    val videoRaw: Int = 0,
    val videoUrl: String,
    val videoType: Int
) : Parcelable {}