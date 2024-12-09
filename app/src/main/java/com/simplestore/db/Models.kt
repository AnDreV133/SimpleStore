package com.simplestore.db

object Models {
    class History(
        val checkListId: Long,
        val productName: String,
        val amount: Double,
        val quantityToAssess: String,
        val cost: Double
    ) {
        object ColName {
            const val CHECK_LIST_ID = "checkListId"
            const val PRODUCT_NAME = "productName"
            const val AMOUNT = "amount"
            const val QUANTITY_TO_ASSESS = "quantityToAssess"
            const val COST = "cost"
        }
    }

    class Assortment(
        val article: Long,
        val productName: String,
        val amount: Double,
        val quantityToAssess: Double,
        val cost: Double
    ) {
        object ColName {
            const val ARTICLE: String = "article"
            const val PRODUCT_NAME: String = "productName"
            const val AMOUNT: String = "amount"
            const val QUANTITY_TO_ASSESS: String = "quantityToAssess"
            const val COST: String = "cost"
        }
    }

    class Rating(
        val productName: String,
        val amountInPercent: Double
    ) {
        object ColName {
            const val PRODUCT_NAME: String = "productName"
            const val AMOUNT_IN_PERCENT: String = "amountInPercent"
        }
    }
}