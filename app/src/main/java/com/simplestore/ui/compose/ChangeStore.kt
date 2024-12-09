package com.simplestore.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.simplestore.db.AppDatabase
import com.simplestore.ui.compose.menu.DrawerMenu
import kotlinx.coroutines.launch

object ChangeStore {
    class Model(val id: Long, val address: String)

    @Composable
    fun Screen(conn: AppDatabase, state: MutableState<StateManager.State>) {
        val scope = rememberCoroutineScope()
        val storesMenu = remember {
            mutableStateListOf<Pair<String, StateManager.State>>()
        }

        DrawerMenu(
            menuItems = storesMenu,
            state = state
        )

        LaunchedEffect(Unit) {
            scope.launch {
                storesMenu.clear()
                storesMenu.addAll(
                    conn.executeStores()
                        .map { model ->
                            "id: ${model.id} by address: ${model.address}" to
                                    StateManager.State.Menu(model.id)
                        }
                )
            }
        }
    }

    private suspend fun AppDatabase.executeStores(): List<Model> =
        this.storeDao()
            .getStores()
            .map {
                Model(it.id, it.address)
            }
}