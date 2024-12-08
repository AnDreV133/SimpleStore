package com.simplestore.ui.compose

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Serializable
import java.util.Stack

object StateManager {

    sealed class State : Serializable {
        class NoConnection : State()
        class ChangeStore : State()
        class History(val storeId: Long) : State()
        class Menu(val storeId: Long) : State()
        class Shopping(val storeId: Long) : State()
        class Rating(val storeId: Long) : State()
        class Console : State()
    }

    private val stackState = Stack<State>()

    @Composable
    fun Screen(conn: SQLiteDatabase?) {
        val state = rememberSaveable {
            mutableStateOf(
                if (conn != null) State.ChangeStore()
                else State.NoConnection()
            )
        }


        val activity = (LocalContext.current as? Activity)
        BackHandler {
            rollback(state, activity)
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
                Menu.Screen(castedState.storeId, state)
            }

            is State.History -> {
                val castedState = state.value as State.History
                stackState.push(castedState)
                History.Screen(conn!!, castedState.storeId)
            }

            is State.Shopping -> {
                val castedState = state.value as State.Shopping
                stackState.push(castedState)
                Shopping.Screen(conn!!, castedState.storeId) { rollback(state, activity) }
            }

            is State.Rating -> {
                val castedState = state.value as State.Rating
                stackState.push(castedState)
                Rating.Screen(conn!!, castedState.storeId)
            }

            is State.Console -> {
                Console.Screen(conn!!)
            }
        }
    }

    private fun rollback(state: MutableState<State>, activity: Activity? = null) {
        if (stackState.size >= 1) {
            stackState.pop()
            state.value = stackState.pop()
        } else
            activity?.finish()
    }

}