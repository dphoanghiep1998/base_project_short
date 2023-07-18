package com.neko.hiepdph.skibyditoiletvideocall.data.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neko.hiepdph.skibyditoiletvideocall.data.database.entity.GalleryEntity
import com.neko.hiepdph.skibyditoiletvideocall.data.model.GalleryModel

@Dao
interface GalleryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGallery(galleryEntity: GalleryEntity)

    @Update
    fun updateGallery(galleryEntity: GalleryEntity)


    @Query("delete from gallery where id=:id")
    fun deleteGallery(id: Int)

    @Query("select * from gallery order by id desc")
    fun getAllGallery(): LiveData<List<GalleryModel>>


}