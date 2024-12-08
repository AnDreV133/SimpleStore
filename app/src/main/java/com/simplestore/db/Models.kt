package com.simplestore.db

object Models {
    class History(
        val checkListId: Long,
        val productName: String,
        val amount: Double,
        val quantityToAssess: String,
        val cost: Double
    )

    class Assortment(
        val article: Long,
        val productName: String,
        val amount: Double,
        val quantityToAssess: Double,
        val cost: Double
    )
}