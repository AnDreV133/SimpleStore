package com.simplestore.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.simplestore.R
import com.simplestore.db.AppDatabase
import com.simplestore.db.Models
import com.simplestore.db.PurchaseEntity
import com.simplestore.ui.compose.table.TableCell
import com.simplestore.ui.compose.table.TableCellText
import kotlinx.coroutines.launch

object Shopping {
//    private class Model(
//        val article: Long,
//        val productName: String,
//        val amount: Double,
//        val quan: String,
//        val price: Double
//    )

    class PurchaseModel(
        val article: Long,
        val productName: String,
        var amount: Double,
        val maxAmount: Double
    )

    @Composable
    fun Screen(conn: AppDatabase, storeId: Long, rollback: () -> Unit) {
        val scope = rememberCoroutineScope()
        val changedProducts = remember { mutableStateListOf<PurchaseModel>() }

        val changeProduct = remember {
            { model: Models.Assortment ->
                if (changedProducts.none { it.article == model.article })
                    changedProducts.add(
                        PurchaseModel(
                            model.article,
                            model.productName,
                            1.0,
                            model.amount
                        )
                    )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TableOfProducts(
                conn = conn,
                storeId = storeId,
                changeProduct = changeProduct
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_downward_24),
                    contentDescription = "arrow"
                )
            }

            val articleWeight = 0.1f
            val productNameWeight = 0.4f
            LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
                items(changedProducts) { model ->
                    Row(modifier = Modifier.clickable {
                        changedProducts.removeIf { it.article == model.article }
                    }) {
                        TableCellText(weight = articleWeight, text = model.article.toString())
                        TableCellText(weight = productNameWeight, text = model.productName)
                        TableCell(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            weight = 0.5f
                        ) {
                            var amount by remember { mutableStateOf(model.amount) }
                            val buttonModifier = { action: () -> Unit ->
                                Modifier
                                    .size(20.dp)
                                    .clickable {
                                        action()
                                        amount = model.amount
                                    }
                            }
                            Icon(
                                modifier = buttonModifier {
                                    if (model.amount > 0) model.amount -= 1
                                },
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_arrow_left_24),
                                contentDescription = "current amount ${model.amount}" // TODO: redraw when add amount
                            )
                            Text(modifier = Modifier.padding(8.dp, 0.dp), text = "$amount")
                            Icon(
                                modifier = buttonModifier {
                                    if (model.amount + 1 - 0.001 < model.maxAmount) model.amount += 1
                                },
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_arrow_right_24),
                                contentDescription = "current amount ${model.amount}"
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(onClick = {
                    if (changedProducts.isNotEmpty()) {
                        scope.launch {
                            conn.executeBuy(storeId, changedProducts)
                        }
                        rollback()
                    }
                }) {
                    Text(text = "Купить")
                }

            }
        }
    }

    @Composable
    private fun TableOfProducts(
        conn: AppDatabase,
        storeId: Long,
        changeProduct: (Models.Assortment) -> Unit
    ) {
        val assortment = remember {
            mutableStateListOf<Models.Assortment>()
        }

        val articleWeight = 0.1f
        val productNameWeight = 0.4f
        val amountWeight = 0.18f
        val quanWeight = 0.16f
        val priceWeight = 0.16f
        LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
            item(assortment.size) {
                Row {
                    TableCellText(weight = articleWeight, text = "Н")
                    TableCellText(weight = productNameWeight, text = "название товара")
                    TableCellText(weight = amountWeight, text = "Кол-во")
                    TableCellText(weight = quanWeight, text = "Исч.")
                    TableCellText(weight = priceWeight, text = "Цена")
                }
            }
            items(assortment) { model ->
                Row(modifier = Modifier.clickable { changeProduct(model) }) {
                    TableCellText(weight = articleWeight, text = model.article.toString())
                    TableCellText(weight = productNameWeight, text = model.productName)
                    TableCellText(weight = amountWeight, text = model.amount.toString())
                    TableCellText(weight = quanWeight, text = model.quantityToAssess.toString())
                    TableCellText(weight = priceWeight, text = model.cost.toString())
                }
            }
        }

        LaunchedEffect(Unit) {
            assortment.clear()
            assortment.addAll(conn.queryAssortment(storeId))
        }
    }

    private suspend fun AppDatabase.queryAssortment(storeId: Long): List<Models.Assortment> {
        return bigQueryDao().getAssortment(storeId)
    }

    private suspend fun AppDatabase.executeBuy(
        storeId: Long,
        purchases: List<PurchaseModel>,
    ) {
        productDao().executeBuy(
            storeId,
            purchases.map {
                PurchaseEntity(
                    productArticle = it.article,
                    amount = it.amount,
                )
            })
    }
}
















