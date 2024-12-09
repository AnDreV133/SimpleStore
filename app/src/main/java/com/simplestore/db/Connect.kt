package com.simplestore.db

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.SQLException
import java.util.concurrent.Executors

private const val TAG = "CONN"

private var appDb: AppDatabase? = null

private class AppDbCallback: RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d(TAG, "onCreate db called")

        if (appDb == null) {
            Log.e(TAG, "db not be created")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            appDb!!.initDao().initializeDatabase()
        }
    }
}


fun connect(applicationContext: Context) =
    try {
        appDb ?: Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        )
            .addCallback(AppDbCallback())
            .setQueryCallback(
                { sqlQuery, _ -> Log.d(TAG, "SQL Query: $sqlQuery") },
                Executors.newSingleThreadExecutor()
            )
            .build()
            .also { appDb = it }
    } catch (e: SQLException) {
        Log.e(TAG, e.toString())
        null
    }