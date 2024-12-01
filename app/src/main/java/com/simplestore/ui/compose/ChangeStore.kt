package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simplestore.db.Table
import com.simplestore.db.query

object ChangeStore {
    class Model(val id: Long, val address: String)

    @Composable
    fun Screen(conn: SQLiteDatabase, state: MutableState<StateManager.State>) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp, 10.dp)
        ) {
            items(conn.executeStores()) { model ->
                Row(modifier = Modifier.padding(10.dp)) {
                    Button(onClick = {
                        state.value = StateManager.State.Menu(conn, model.id)
                    }) {
                        Text(text = "id: ${model.id} by address: ${model.address}")
                    }
                }
            }
        }
    }

    private fun SQLiteDatabase.executeStores(): MutableList<Model> =
        mutableListOf<Model>().apply {
            this@executeStores.query("select * from ${Table.Store.T_NAME};") {
                if (it == null) return@query

                while (it.moveToNext()) {
                    add(Model(
                        it.getLong(0),
                        it.getString(1)
                    ))
                }
            }
        }
}