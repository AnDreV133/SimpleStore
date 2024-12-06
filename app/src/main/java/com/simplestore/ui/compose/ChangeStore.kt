package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.simplestore.db.AppDatabase
import com.simplestore.db.Table
import com.simplestore.db.query
import com.simplestore.ui.compose.ChangeStore.executeStores
import com.simplestore.ui.compose.menu.DrawerMenu

object ChangeStore {
    class Model(val id: Long, val address: String)

    @Composable
    fun Screen(conn: AppDatabase, state: MutableState<StateManager.State>) {
        DrawerMenu(
            menuItems = conn.executeStores().map { model ->
                "id: ${model.id} by address: ${model.address}" to
                        StateManager.State.Menu(model.id)
            },
            state = state
        )
    }

    private suspend fun AppDatabase.executeStores(): List<Model> =
        this.storeDao().getStores().map { Model(it.id, it.address) }
}