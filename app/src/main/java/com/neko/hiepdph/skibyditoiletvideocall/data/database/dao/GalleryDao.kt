package com.neko.hiepdph.skibyditoiletvideocall.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.neko.hiepdph.skibyditoiletvideocall.data.database.entity.GalleryEntity
import com.neko.hiepdph.skibyditoiletvideocall.data.model.GalleryModel


@Dao
interface GalleryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGallery(galleryEntity: GalleryEntity)

    @Update
    fun updateGallery(galleryEntity: GalleryEntity)

    @Query("DELETE FROM sqlite_sequence WHERE name='gallery'")
    fun resetPrimaryKey()

    @Query("delete from gallery where id=:id")
    fun deleteGallery(id: Int)

    @RawQuery
    fun vacuumDb(supportSQLiteQuery: SupportSQLiteQuery): Int

    @Query("delete from gallery")
    fun deleteAll()

    @Query("select * from gallery order by id desc")
    fun getAllGallery(): LiveData<List<GalleryModel>>


}