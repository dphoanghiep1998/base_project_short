package com.neko.hiepdph.skibyditoiletvideocall.di


import android.content.Context
import androidx.room.Room
import com.neko.hiepdph.skibyditoiletvideocall.common.Constant
import com.neko.hiepdph.skibyditoiletvideocall.data.database.AppDatabase
import com.neko.hiepdph.skibyditoiletvideocall.data.database.dao.GalleryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, Constant.APP_DB).fallbackToDestructiveMigration().build()
    }


    @Provides
    fun provideGalleryDao(appDatabase: AppDatabase): GalleryDao {
        return appDatabase.galleryDao
    }







}