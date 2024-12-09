package com.simplestore.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [
        StoreEntity::class,
        ProductEntity::class,
        CheckListEntity::class,
        AccountingEntity::class,
        PurchaseEntity::class,
    ],
    exportSchema = true,
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun initDao(): InitDao
    abstract fun storeDao(): StoreDao
    abstract fun productDao(): ProductDao
    abstract fun bigQueryDao(): BigQueryDao
}