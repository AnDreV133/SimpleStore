package com.simplestore.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [InitDao::class, StoreDao::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun initDao(): InitDao
    abstract fun storeDao(): StoreDao
}