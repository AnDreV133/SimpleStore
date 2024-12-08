package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.Composable
import com.simplestore.db.AppDatabase

@Composable
fun MainScreen(conn : AppDatabase?) {
    StateManager.Screen(conn)
}