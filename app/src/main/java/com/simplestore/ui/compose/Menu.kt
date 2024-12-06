package com.simplestore.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.simplestore.ui.compose.menu.DrawerMenu

object Menu {
    @Composable
    fun Screen(storeId: Long, state: MutableState<StateManager.State>) {
        DrawerMenu(
            menuItems = listOf(
                "Сделать покупку" to StateManager.State.Shopping(storeId),
                "История покупок" to StateManager.State.History(storeId),
                "Рейтинг товаров по магазину" to StateManager.State.Rating(storeId),
            ),
            state = state
        )
    }
}
