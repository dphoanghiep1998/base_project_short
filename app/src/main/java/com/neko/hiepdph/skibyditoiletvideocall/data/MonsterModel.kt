package com.neko.hiepdph.skibyditoiletvideocall.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonsterModel(
    val id :Int,
    val image: Int,
    val content: String,
    val isRewardContent:Boolean = false
) : Parcelable {}