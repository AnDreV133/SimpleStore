package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simplestore.db.Purchase
import com.simplestore.db.Table
import com.simplestore.db.query

object Menu {

    @Composable
    fun Screen(conn: SQLiteDatabase, storeId: Long, state: MutableState<StateManager.State>) {

//        Выберите действие:
//                    1. Сделать покупку
//                    2. История покупок
//                    3. Рейтинг товаров по магазину
//                    4. Выход

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(onClick = {
                state.value = StateManager.State.Shopping(conn, storeId)
            }) {
                Text("Go shopping")
            }
            Button(onClick = {
                state.value = StateManager.State.History(conn, storeId)
            }) {
                Text("History")
            }
        }
    }


}
