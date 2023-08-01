package com.neko.hiepdph.skibyditoiletvideocall.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OtherCallModel(
    val monster_id :Int,
    val image: Int,
    val circleImage: Int,
    val name: String,
    val videoRaw: Int = 0,
    val videoUrl: String,
    val videoType: Int
) : Parcelable {}