package com.neko.hiepdph.skibyditoiletvideocall.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OtherCallModel(val image: Int, val name: String, val videoRaw: Int, val videoType: Int) :
    Parcelable {
}