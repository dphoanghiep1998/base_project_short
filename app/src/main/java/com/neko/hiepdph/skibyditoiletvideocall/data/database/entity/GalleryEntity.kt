package com.neko.hiepdph.skibyditoiletvideocall.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery")
data class GalleryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = -1,
    val name: String,
    val time: Long,
    val videoId :Int,
    val videoLength: Long,
    val videoPath: Int,
    val videoUrl:String,
    val cameraVideoPath: String,
    val videoType: Int
) {}