package com.neko.hiepdph.skibyditoiletvideocall.data.database.repositories

import androidx.lifecycle.LiveData
import com.neko.hiepdph.skibyditoiletvideocall.data.database.services.GalleryService
import com.neko.hiepdph.skibyditoiletvideocall.data.model.GalleryModel
import com.neko.hiepdph.skibyditoiletvideocall.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepo @Inject constructor(
    private val galleryService: GalleryService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    fun getALlGallery(): LiveData<List<GalleryModel>> =
        galleryService.galleryDao.getAllGallery()

    suspend fun deleteGallery(id: Int) = withContext(dispatcher) {
        galleryService.galleryDao.deleteGallery(id)
    }

    suspend fun insertGallery(galleryModel: GalleryModel) = withContext(dispatcher) {
        galleryService.galleryDao.insertGallery(galleryModel.toGalleryEntity())
    }
}
