package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.simplestore.db.Table
import com.simplestore.db.query
import com.simplestore.ui.compose.menu.DrawerMenu

object ChangeStore {
    class Model(val id: Long, val address: String)

    @Composable
    fun Screen(conn: SQLiteDatabase, state: MutableState<StateManager.State>) {
        DrawerMenu(
            menuItems = conn.executeStores().map { model ->
                "id: ${model.id} by address: ${model.address}" to
                        StateManager.State.Menu(model.id)
            },
            state = state
        )
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