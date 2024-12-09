package com.simplestore.db

object Models {
    object HistoryName {
        const val CHECK_LIST_ID = "checkListId"
        const val PRODUCT_NAME = "productName"
        const val AMOUNT = "amount"
        const val QUANTITY_TO_ASSESS = "quantityToAssess"
        const val COST = "cost"
    }

    class History(
        val checkListId: Long,
        val productName: String,
        val amount: Double,
        val quantityToAssess: String,
        val cost: Double
    )

    object AssortmentName {
        const val ARTICLE: String = "article"
        const val PRODUCT_NAME: String = "productName"
        const val AMOUNT: String = "amount"
        const val QUANTITY_TO_ASSESS: String = "quantityToAssess"
        const val COST: String = "cost"
    }

    class Assortment(
        val article: Long,
        val productName: String,
        val amount: Double,
        val quantityToAssess: Double,
        val cost: Double
    )
}