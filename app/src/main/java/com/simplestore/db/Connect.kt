package com.simplestore.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.SQLException

class SQLiteConnection(
    baseContext: Context
) : SQLiteOpenHelper(baseContext, "app.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        Log.d("CONN", "Created")
        try {
            db.beginTransaction()
            db.executeInitDb()
            db.executePrepareDb()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        Log.d("CONN", "Prepared")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.executeDeleteDb()
        onCreate(db)
        Log.d("CONN", "Upgraded")
    }
}

fun connect(baseContext: Context): SQLiteDatabase? =
    try {
        SQLiteConnection(baseContext).apply { onCreate(writableDatabase) }.writableDatabase
    } catch (e: SQLException) {
        Log.e("CONN", e.toString())
        null
    }