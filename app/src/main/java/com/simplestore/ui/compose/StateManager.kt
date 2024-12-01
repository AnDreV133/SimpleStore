package com.simplestore.ui.compose

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Stack

object StateManager {
    open class State {
        class NoConnection : State()
        class ChangeStore(val conn: SQLiteDatabase) : State()
        class History(val conn: SQLiteDatabase, val storeId: Long) : State()
        class Menu(val conn: SQLiteDatabase, val storeId: Long) : State()
        class Shopping(val conn: SQLiteDatabase, val storeId: Long) : State()
        class Exit(val conn: SQLiteDatabase? = null) : State()
    }

    private val stackState = Stack<State>()

    @Composable
    fun Screen(conn: SQLiteDatabase?) {
        val state = remember {
            mutableStateOf(
                if (conn != null) State.ChangeStore(conn)
                else State.NoConnection()
            )
        }

        val activity = (LocalContext.current as? Activity)
        BackHandler {
            if (stackState.size >= 1) {
                stackState.pop()
                state.value = stackState.pop()
            } else
                activity?.finish()
        }

        when (state.value) {
            is State.NoConnection -> {
                stackState.push(state.value as State.NoConnection)
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    text = "No connection"
                )
            }

            is State.ChangeStore -> {
                stackState.push(state.value as State.ChangeStore)
                ChangeStore.Screen(conn!!, state)
            }

            is State.Menu -> {
                val castedState = state.value as State.Menu
                stackState.push(castedState)
                Menu.Screen(conn!!, castedState.storeId, state)
            }

            is State.History -> {
                val castedState = state.value as State.History
                stackState.push(castedState)
                History.Screen(conn!!, castedState.storeId)
            }
            is State.Shopping -> {
                val castedState = state.value as State.Shopping
                stackState.push(castedState)
                Shopping.Screen(conn!!, castedState.storeId)
            }
        }
    }

}