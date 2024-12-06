package com.simplestore.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction

@Dao
interface InitDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStores(stores: List<StoreEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccounting(accounting: List<AccountingEntity>)

    @Transaction
    suspend fun initializeDatabase() {
        val stores = listOf(
            StoreEntity(id = 1, address = "LA, 5 Avenue"),
            StoreEntity(id = 2, address = "LA, 11 Avenue"),
            StoreEntity(id = 3, address = "LA, 12 Avenue")
        )

        val products = listOf(
            ProductEntity(article = 1, name = "Колбаса докторская", category = "meat", quantityToAssess = "100g"),
            ProductEntity(article = 2, name = "Сырок плавленный", category = "milk", quantityToAssess = "p"),
            ProductEntity(article = 3, name = "Молоко, бутылка 1л", category = "milk", quantityToAssess = "p"),
            ProductEntity(article = 4, name = "Булочка \"Лакомка\"", category = "bake", quantityToAssess = "p"),
            ProductEntity(article = 5, name = "Стейк, говяжий", category = "meat", quantityToAssess = "p")
        )

        val accounting = listOf(
            AccountingEntity(storeId = 1, productArticle = 1, cost = 100.0, amount = 700),
            AccountingEntity(storeId = 1, productArticle = 2, cost = 30.0, amount = 300),
            AccountingEntity(storeId = 1, productArticle = 3, cost = 70.0, amount = 300),
            AccountingEntity(storeId = 1, productArticle = 4, cost = 35.0, amount = 200),
            AccountingEntity(storeId = 1, productArticle = 5, cost = 200.0, amount = 100),
            AccountingEntity(storeId = 2, productArticle = 1, cost = 190.0, amount = 700),
            AccountingEntity(storeId = 2, productArticle = 2, cost = 39.0, amount = 300),
            AccountingEntity(storeId = 2, productArticle = 3, cost = 79.0, amount = 300),
            AccountingEntity(storeId = 2, productArticle = 4, cost = 39.0, amount = 200),
            AccountingEntity(storeId = 2, productArticle = 5, cost = 290.0, amount = 100)
        )

        insertStores(stores)
        insertProducts(products)
        insertAccounting(accounting)
    }
}

@Dao
interface ProductDao {}

@Dao
interface StoreDao {
    suspend fun getStores(): List<StoreEntity>
}

//fun SQLiteDatabase.query(query: String, handler: (Cursor?) -> Unit = {}) {
//    handler(query(query))
//}
//
//fun SQLiteDatabase.query(query: String): Cursor? {
//    return rawQuery(query, null)
//}
//
//fun SQLiteDatabase.execute(query: String) {
//    execSQL(query)
//}