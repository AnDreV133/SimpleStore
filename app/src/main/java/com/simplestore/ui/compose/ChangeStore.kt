package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.simplestore.db.Table
import com.simplestore.db.query
import com.simplestore.ui.compose.menu.DrawerMenu

object ChangeStore {
    class Model(val id: Long, val address: String)

    @Composable
    fun Screen(conn: SQLiteDatabase, state: MutableState<StateManager.State>) {
        Column(
            modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight(),
                fontSize = 36.sp,
                text = "Меню:"
            )

            DrawerMenu(
                modifier = Modifier
                    .wrapContentSize(),
                menuItems = listOf(
                    "Консоль для запросов" to StateManager.State.Console()
                ),
                state = state
            )

            Text(
                modifier = Modifier
                    .wrapContentHeight(),
                fontSize = 36.sp,
                text = "Список магазинов:"
            )

            DrawerMenu(
                modifier = Modifier
                    .wrapContentSize(),
                menuItems = conn.executeStores().map { model ->
                    "id: ${model.id} by address: ${model.address}" to
                            StateManager.State.Menu(model.id)
                },
                state = state
            )
        }
    }


    private fun SQLiteDatabase.executeStores(): MutableList<Model> =
        mutableListOf<Model>().apply {
            this@executeStores.query("select * from ${Table.Store.T_NAME};") {
                if (it == null) return@query

                while (it.moveToNext()) {
                    add(
                        Model(
                            it.getLong(0),
                            it.getString(1)
                        )
                    )
                }
            }
        }
}