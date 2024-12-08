package com.simplestore.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
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
            ProductEntity(
                article = 1,
                name = "Колбаса докторская",
                category = "meat",
                quantityToAssess = "100g"
            ),
            ProductEntity(
                article = 2,
                name = "Сырок плавленный",
                category = "milk",
                quantityToAssess = "p"
            ),
            ProductEntity(
                article = 3,
                name = "Молоко, бутылка 1л",
                category = "milk",
                quantityToAssess = "p"
            ),
            ProductEntity(
                article = 4,
                name = "Булочка \"Лакомка\"",
                category = "bake",
                quantityToAssess = "p"
            ),
            ProductEntity(
                article = 5,
                name = "Стейк, говяжий",
                category = "meat",
                quantityToAssess = "p"
            )
        )

        val accounting = listOf(
            AccountingEntity(storeId = 1, productArticle = 1, cost = 100.0, amount = 700.0),
            AccountingEntity(storeId = 1, productArticle = 2, cost = 30.0, amount = 300.0),
            AccountingEntity(storeId = 1, productArticle = 3, cost = 70.0, amount = 300.0),
            AccountingEntity(storeId = 1, productArticle = 4, cost = 35.0, amount = 200.0),
            AccountingEntity(storeId = 1, productArticle = 5, cost = 200.0, amount = 100.0),
            AccountingEntity(storeId = 2, productArticle = 1, cost = 190.0, amount = 700.0),
            AccountingEntity(storeId = 2, productArticle = 2, cost = 39.0, amount = 300.0),
            AccountingEntity(storeId = 2, productArticle = 3, cost = 79.0, amount = 300.0),
            AccountingEntity(storeId = 2, productArticle = 4, cost = 39.0, amount = 200.0),
            AccountingEntity(storeId = 2, productArticle = 5, cost = 290.0, amount = 100.0)
        )

        insertStores(stores)
        insertProducts(products)
        insertAccounting(accounting)
    }
}

@Dao
abstract class ProductDao {
    @Query(
        """
        INSERT INTO ${Table.CheckList.T_NAME} 
            (${Table.CheckList.STORE_ID}, ${Table.CheckList.TIME})
            VALUES (:storeId, CURRENT_TIMESTAMP);
        """
    )
    protected abstract suspend fun insertCheckList(storeId: Long): Long

    @Insert
    protected abstract suspend fun insertPurchase(purchase: PurchaseEntity): Long

    @Query(
        """
        UPDATE ${Table.Accounting.T_NAME}
            SET ${Table.Accounting.AMOUNT}=${Table.Accounting.AMOUNT}-:amount
            WHERE ${Table.Accounting.STORE_ID}=:storeId 
                AND ${Table.Accounting.PRODUCT_ARTICLE}=:article;
        """
    )
    protected abstract suspend fun updateAccounting(
        storeId: Long,
        article: Long,
        amount: Double
    ): List<CheckListEntity>

    @Transaction
    suspend fun executeBuy(
        storeId: Long,
        purchases: List<PurchaseEntity>
    ) {
        val checkListId = insertCheckList(storeId)
        purchases
            .map { it.copy(checkListId = checkListId) }
            .forEach {
                insertPurchase(it)
                updateAccounting(
                    storeId,
                    it.productArticle,
                    it.amount
                )
            }
    }
}

@Dao
interface StoreDao {
    suspend fun getStores(): List<StoreEntity>
}

@Dao
abstract class BigQueryDao {


    suspend fun getHistory(storeId: Long): List<Models.History> = rawQuery(
        RoomRawQuery(
            """
            select  t0.${Table.CheckList.ID},
                    t2.${Table.Product.NAME}, 
                    t1.${Table.Purchase.AMOUNT},
                    t2.${Table.Product.QUANTITY_TO_ASSESS},
                    t1.${Table.Purchase.AMOUNT}*t3.${Table.Accounting.COST}
            from ${Table.CheckList.T_NAME} as t0 
            inner join ${Table.Purchase.T_NAME} as t1
            on t0.${Table.CheckList.ID}=t1.${Table.Purchase.CHECK_LIST_ID}
                and t0.${Table.CheckList.STORE_ID}=$storeId
            inner join ${Table.Product.T_NAME} as t2
            on t1.${Table.Purchase.T_NAME}=t2.${Table.Product.ARTICLE}   
            inner join ${Table.Accounting.T_NAME} as t3
            on t1.${Table.Purchase.PRODUCT_ARTICLE}=t3.${Table.Accounting.PRODUCT_ARTICLE}
                and t3.${Table.Accounting.STORE_ID}=$storeId
            order by t0.${Table.CheckList.ID} asc;
            """
        ) {
            Models.History(
                checkListId = it.getLong(0),
                productName = it.getText(1),
                amount = it.getDouble(2),
                quantityToAssess = it.getText(3),
                cost = it.getDouble(4)
            )
        }
    )

    suspend fun getAssortment(storeId: Long): List<Models.Assortment> = rawQuery(
        RoomRawQuery(
            """
            select  t0.${Table.Accounting.PRODUCT_ARTICLE},
                    t1.${Table.Product.NAME},
                    t0.${Table.Accounting.AMOUNT}, 
                    t1.${Table.Product.QUANTITY_TO_ASSESS},
                    t0.${Table.Accounting.COST}
                from ${Table.Accounting.T_NAME} as t0
                inner join ${Table.Product.T_NAME} as t1 
                on t0.${Table.Accounting.PRODUCT_ARTICLE}=t1.${Table.Product.ARTICLE}
                where ${Table.Accounting.STORE_ID}=$storeId;
            """
        ) {
            Models.Assortment(
                article = it.getLong(0),
                productName = it.getText(1),
                amount = it.getDouble(2),
                quantityToAssess = it.getDouble(3),
                cost = it.getDouble(4)
            )
        }
    )

    @RawQuery
    protected abstract suspend fun <T> rawQuery(sql: RoomRawQuery): List<T>
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