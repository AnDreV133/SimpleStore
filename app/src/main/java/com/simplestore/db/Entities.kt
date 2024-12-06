package com.simplestore.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Table.Store.T_NAME)
data class StoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
    @ColumnInfo(name = Table.Store.ADDRESS)
    val address: String
)

@Entity(tableName = Table.Product.T_NAME)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val article: Long = -1,
    @ColumnInfo(name = Table.Product.NAME)
    val name: String,
    @ColumnInfo(name = Table.Product.CATEGORY)
    val category: String?,
    @ColumnInfo(name = Table.Product.QUANTITY_TO_ASSESS)
    val quantityToAssess: String
)

@Entity(tableName = Table.CheckList.T_NAME)
data class CheckListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
    @ColumnInfo(name = Table.CheckList.STORE_ID)
    val storeId: Int?,
    @ColumnInfo(name = Table.CheckList.TIME)
    val time: Long
)

@Entity(
    tableName = Table.Accounting.T_NAME,
    primaryKeys = [
        Table.Accounting.STORE_ID,
        Table.Accounting.PRODUCT_ARTICLE
    ]
)
data class AccountingEntity(
    @ColumnInfo(name = Table.Accounting.STORE_ID)
    val storeId: Long,
    @ColumnInfo(name = Table.Accounting.PRODUCT_ARTICLE)
    val productArticle: Int,
    @ColumnInfo(name = Table.Accounting.COST)
    val cost: Double?,
    @ColumnInfo(name = Table.Accounting.AMOUNT)
    val amount: Int
)

@Entity(tableName = Table.Purchase.T_NAME)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
    @ColumnInfo(name = Table.Purchase.CHECK_LIST_ID)
    val checkListId: Int?,
    @ColumnInfo(name = Table.Purchase.PRODUCT_ARTICLE)
    val productArticle: Int,
    @ColumnInfo(name = Table.Purchase.AMOUNT)
    val amount: Double
)