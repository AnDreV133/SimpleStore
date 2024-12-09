package com.simplestore.ui.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simplestore.db.AppDatabase
import com.simplestore.db.Models
import com.simplestore.ui.compose.table.TableCellText
import androidx.compose.foundation.lazy.items


object Rating {
    @Composable
    fun Screen(conn: AppDatabase, storeId: Long) {
        val products = remember {
            mutableStateListOf<Models.Rating>()
        }

        val nameWeight = 0.7f
        val partInPercentWeight = 0.3f
        LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
            item(products.size) {
                Row {
                    TableCellText(weight = nameWeight, text = "Название продукта")
                    TableCellText(weight = partInPercentWeight, text = "%")
                }
            }
            items(products) { model ->
                Row {
                    TableCellText(weight = nameWeight, text = model.productName)
                    TableCellText(weight = partInPercentWeight, text = "${model.amountInPercent}%")
                }
            }
        }

        LaunchedEffect(Unit) {
            products.clear()
            products.addAll(conn.executeStatistic(storeId))
        }
    }

    private suspend fun AppDatabase.executeStatistic(storeId: Long): List<Models.Rating> =
        bigQueryDao().getRating(storeId)
}
