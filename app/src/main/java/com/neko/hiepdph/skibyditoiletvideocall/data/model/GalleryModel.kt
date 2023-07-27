package com.neko.hiepdph.skibyditoiletvideocall.data.model

import android.os.Parcelable
import com.neko.hiepdph.skibyditoiletvideocall.data.database.entity.GalleryEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GalleryModel(
    val id: Int = -1,
    val name: String,
    val time: Long,
    val videoLength: Long,
    val videoPath: Int,
    val videoUrl:String,
    val cameraVideoPath: String,
    val videoType: Int
) : Parcelable {
    fun toGalleryEntity(): GalleryEntity {
        val fileId = if (id == -1) 0 else id
        return GalleryEntity(
            fileId, name, time, videoLength, videoPath,videoUrl, cameraVideoPath, videoType
        )
    }
}