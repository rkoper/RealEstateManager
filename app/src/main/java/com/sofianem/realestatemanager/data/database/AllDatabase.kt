package com.sofianem.realestatemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sofianem.realestatemanager.data.dao.EstateDao
import com.sofianem.realestatemanager.data.dao.ImageDao
import com.sofianem.realestatemanager.data.dao.PlaceDao
import com.sofianem.realestatemanager.data.model.EstateR
import com.sofianem.realestatemanager.data.model.ImageV
import com.sofianem.realestatemanager.data.model.NearbyPlaces

@Database(entities = (arrayOf(ImageV::class, EstateR::class, NearbyPlaces::class)), version = 1, exportSchema = false)
abstract class AllDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao
    abstract fun estateDao(): EstateDao
    abstract fun nearbyPlaceDao() : PlaceDao

    companion object {
        private var INSTANCE: AllDatabase? = null

        fun getInstance(context: Context): AllDatabase {
            if (INSTANCE == null) { synchronized(this) { INSTANCE = Room.databaseBuilder(
                context.applicationContext, AllDatabase::class.java, "RealEstateManager.db").allowMainThreadQueries().build() } }
            return INSTANCE as AllDatabase
        } } }
