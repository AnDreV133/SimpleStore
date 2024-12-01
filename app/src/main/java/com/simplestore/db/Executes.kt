package com.simplestore.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

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

fun SQLiteDatabase.executeDeleteDb(): Unit =
    listOf(
        "DROP TABLE IF EXISTS ${Table.Purchase.T_NAME};",
        "DROP TABLE IF EXISTS ${Table.Accounting.T_NAME};",
        "DROP TABLE IF EXISTS ${Table.CheckList.T_NAME} CASCADE;",
        "DROP TABLE IF EXISTS ${Table.Store.T_NAME} CASCADE;",
        "DROP TABLE IF EXISTS ${Table.Product.T_NAME} CASCADE;"
    ).forEach { execute(it) }

fun SQLiteDatabase.executeInitDb() {
    listOf(
        """   
        -- таблица магазинов
        CREATE TABLE IF NOT EXISTS ${Table.Store.T_NAME} (
            ${Table.Store.ID} INTEGER PRIMARY KEY, 
            ${Table.Store.ADDRESS} TEXT
        );
        """,
        """
        -- таблица продукции, поставляемой в магазины
        CREATE TABLE IF NOT EXISTS ${Table.Product.T_NAME} (
            ${Table.Product.ARTICLE} INTEGER PRIMARY KEY,
            ${Table.Product.NAME} TEXT NOT NULL,
            ${Table.Product.CATEGORY} TEXT,
            ${Table.Product.QUANTITY_TO_ASSESS} TEXT NOT NULL
        );    
        """,
        """
        -- таблица чеков со связью с магазином
        CREATE TABLE IF NOT EXISTS ${Table.CheckList.T_NAME} (
            ${Table.CheckList.ID} INTEGER NOT NULL PRIMARY KEY,
            ${Table.CheckList.STORE_ID} INTEGER REFERENCES store (id)
                ON DELETE SET NULL, 	-- если удалить магазин,
                                        -- то чек останется
            ${Table.CheckList.TIME} TIMESTAMP NOT NULL
        );    
        """,
        """
        -- таблица учёта
        CREATE TABLE IF NOT EXISTS ${Table.Accounting.T_NAME} (
            --${Table.Accounting} INTEGER NOT NULL PRIMARY KEY,
            ${Table.Accounting.STORE_ID} INTEGER NOT NULL REFERENCES store (id)
                ON DELETE SET NULL,
            ${Table.Accounting.PRODUCT_ARTICLE} INTEGER NOT NULL REFERENCES product (article)
                ON DELETE SET NULL,
            ${Table.Accounting.COST} NUMERIC(6, 2),
            ${Table.Accounting.AMOUNT} INTEGER NOT NULL default 0,
            PRIMARY KEY (${Table.Accounting.STORE_ID}, ${Table.Accounting.PRODUCT_ARTICLE})
        );
        """,
        """
        -- покупка является строкой в чеке
        CREATE TABLE IF NOT EXISTS ${Table.Purchase.T_NAME} (
            ${Table.Purchase.T_NAME} INTEGER NOT NULL PRIMARY KEY,
            ${Table.Purchase.CHECK_LIST_ID} INTEGER NOT NULL REFERENCES check_list (id)
                ON DELETE SET NULL,
            ${Table.Purchase.PRODUCT_ARTICLE} INTEGER NOT NULL REFERENCES product (article)
                ON DELETE SET NULL,
            ${Table.Purchase.AMOUNT} NUMERIC(6, 2) NOT NULL	
        );
        """
    ).forEach { execute(it) }
}


fun SQLiteDatabase.executePrepareDb() {
    listOf(
        """   
    INSERT INTO ${Table.Store.T_NAME} 
            (${Table.Store.ID}, ${Table.Store.ADDRESS}) 
        VALUES 
            (1, 'LA, 5 Avenue'), 
            (2, 'LA, 11 Avenue'),
            (3, 'LA, 12 Avenue')
        ON CONFLICT (${Table.Store.ID}) DO NOTHING;
    """,
        """
    INSERT INTO ${Table.Product.T_NAME}  
            (${Table.Product.ARTICLE}, ${Table.Product.NAME}, ${Table.Product.CATEGORY}, ${Table.Product.QUANTITY_TO_ASSESS})
        VALUES 
            (1, 'Колбаса докторская', 'meat', '100g'),
            (2, 'Сырок плавленный', 'milk', 'p'), 
            (3, 'Молоко, бутылка 1л', 'milk', 'p'),
            (4, 'Булочка "Лакомка"', 'bake', 'p'),
            (5, 'Стейк, говяжий', 'meat', 'p')
        ON CONFLICT (${Table.Product.ARTICLE}) DO NOTHING;
    """,
        """
    INSERT INTO ${Table.Accounting.T_NAME} 
            (${Table.Accounting.STORE_ID}, ${Table.Accounting.PRODUCT_ARTICLE}, ${Table.Accounting.COST}, ${Table.Accounting.AMOUNT})
        VALUES 
            (1, 1, 100, 700),
            (1, 2, 30, 300),
            (1, 3, 70, 300),
            (1, 4, 35, 200),
            (1, 5, 200, 100),
            (2, 1, 190, 700),
            (2, 2, 39, 300),
            (2, 3, 79, 300),
            (2, 4, 39, 200),
            (2, 5, 290, 100)
        ON CONFLICT (${Table.Accounting.STORE_ID}, ${Table.Accounting.PRODUCT_ARTICLE}) DO NOTHING;
    """
    ).forEach { execute(it) }
}



fun SQLiteDatabase.query(query: String, handler: (Cursor?) -> Unit = {}) {
    handler(query(query))
}

fun SQLiteDatabase.query(query: String): Cursor? {
    return rawQuery(query, null)
}

fun SQLiteDatabase.execute(query: String) {
    execSQL(query)
}