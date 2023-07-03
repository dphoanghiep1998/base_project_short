package com.neko.hiepdph.skibyditoiletvideocall.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neko.hiepdph.skibyditoiletvideocall.data.database.dao.GalleryDao
import com.neko.hiepdph.skibyditoiletvideocall.data.database.entity.GalleryEntity

@Database(entities = [GalleryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val galleryDao:GalleryDao
}
