package com.neko.hiepdph.skibyditoiletvideocall.data.model

import com.neko.hiepdph.skibyditoiletvideocall.data.database.entity.GalleryEntity

data class GalleryModel(
    val id: Int = -1,
    val name: String,
    val time: Long,
    val videoLength: Long,
    val videoPath: String,
    val videoType: Int
) {
    fun toGalleryEntity(): GalleryEntity {
        val fileId = if (id == -1) 0 else id
        return GalleryEntity(
            fileId, name, time, videoLength, videoPath, videoType
        )
    }
}