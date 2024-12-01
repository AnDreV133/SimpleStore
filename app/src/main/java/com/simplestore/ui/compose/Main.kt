package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.Composable

@Composable
fun MainScreen(conn : SQLiteDatabase?) {
    StateManager.Screen(conn)
}