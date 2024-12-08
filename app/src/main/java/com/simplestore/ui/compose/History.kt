package com.simplestore.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simplestore.db.AppDatabase
import com.simplestore.db.Models
import kotlinx.coroutines.launch

object History {
    class PurchaseHistoryModel(val checkId: Long, val purchases: List<Models.History>)

    @Composable
    fun Screen(conn: AppDatabase, storeId: Long) {
        val scope = rememberCoroutineScope()
        var historyModelState by remember { mutableStateOf<List<PurchaseHistoryModel>>(listOf()) }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(historyModelState) { model ->
                val modifier = Modifier.padding(bottom = 10.dp)
                Text(
                    modifier = modifier,
                    text = "Check Id: ${model.checkId}"
                )
                for (purchase in model.purchases) {
                    Text(
                        modifier = modifier,
                        text = "Name: ${purchase.productName}, " +
                                "Count: ${purchase.amount} in ${purchase.quantityToAssess}, " +
                                "Price: ${purchase.cost}"
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            scope.launch {
                historyModelState = conn.queryGetHistory(storeId)
            }
        }
    }

    private suspend fun AppDatabase.queryGetHistory(storeId: Long): List<PurchaseHistoryModel> {
        val res = mutableListOf<PurchaseHistoryModel>()
        mutableMapOf<Long, List<Models.History>>().let { groupedPurchaseList ->
            bigQueryDao()
                .getHistory(storeId)
                .forEach { model ->
                    groupedPurchaseList.compute(
                        model.checkListId
                    ) { k, v ->
                        (v?.toMutableList() ?: mutableListOf())
                            .apply { add(model) }
                    }
                }

            for (groupedPurchase in groupedPurchaseList) {
                res.add(PurchaseHistoryModel(groupedPurchase.key, groupedPurchase.value))
            }
        }

        return res
    }
}