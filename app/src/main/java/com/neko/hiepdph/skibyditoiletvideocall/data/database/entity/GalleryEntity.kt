package com.neko.hiepdph.skibyditoiletvideocall.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery")
data class GalleryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = -1,
    val name: String,
    val time: Long,
    val videoLength: Long,
    val videoPath: String,
    val videoType: Int
) {}