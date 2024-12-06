package com.simplestore.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.SQLException

private const val TAG = "CONN"

private class AppDbCallback(
    val appDb: AppDatabase?
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        if (appDb == null) {
            Log.e(TAG, "onCreate")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            appDb.initDao().initializeDatabase()
        }
    }
}

private var appDb: AppDatabase? = null

fun connect(applicationContext: Context) =
    try {
        appDb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        )
            .addCallback(AppDbCallback(appDb))
            .build()
    } catch (e: SQLException) {
        Log.e(TAG, e.toString())
        null
    }