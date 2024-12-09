package com.simplestore.db

object Table {
    object Purchase {
        const val T_NAME = "purchase"

        const val ID = "id"
        const val CHECK_LIST_ID = "check_list_id"
        const val PRODUCT_ARTICLE = "product_article"
        const val AMOUNT = "amount"
    }

    object CheckList {
        const val T_NAME = "check_list"

        const val ID = "id"
        const val STORE_ID = "store_id"
        const val TIME = "time"
    }

    object Accounting {
        const val T_NAME = "accounting"

        const val STORE_ID = "store_id"
        const val PRODUCT_ARTICLE = "product_article"
        const val COST = "cost"
        const val AMOUNT = "amount"
    }

    object Store {
        const val T_NAME = "store"

        const val ID = "id"
        const val ADDRESS = "address"
    }

    object Product {
        const val T_NAME = "product"

        const val ARTICLE = "article"
        const val NAME = "name"
        const val CATEGORY = "category"
        const val QUANTITY_TO_ASSESS = "quantity_to_assess"
    }
}

enum class Category(val value: String) {
    MEAT("meat"),
    MILK("milk"),
    BAKE("bake")
}

enum class QuantityToAssess(val value: String) {
    PIECE("p"),
    GRAM_100("100g"),
    KILOGRAM_1("1kg")
}