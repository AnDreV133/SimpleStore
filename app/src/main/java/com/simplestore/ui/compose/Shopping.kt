package com.simplestore.ui.compose

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.simplestore.R
import com.simplestore.db.Table
import com.simplestore.db.query
import com.simplestore.ui.compose.table.TableCell
import com.simplestore.ui.compose.table.TableCellText
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.coroutines.coroutineContext

object Shopping {
    private class Model(
        val article: Long,
        val productName: String,
        val amount: Double,
        val quan: String,
        val price: Double
    )

    data class Purchase(
        val article: Long,
        val productName: String,
        var amount: Double,
        val maxAmount: Double
    )

    @Composable
    fun Screen(conn: SQLiteDatabase, storeId: Long) {
        val changedProducts = remember { mutableStateListOf<Purchase>() }

        val assortment = conn.queryAssortment(storeId)

        val articleWeight = 0.1f
        val productNameWeight = 0.4f
        val amountWeight = 0.18f
        val quanWeight = 0.16f
        val priceWeight = 0.16f
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
                item(conn.queryAssortment(storeId)) {
                    Row {
                        TableCellText(weight = articleWeight, text = "N")
                        TableCellText(weight = productNameWeight, text = "Product Name")
                        TableCellText(weight = amountWeight, text = "Amount")
                        TableCellText(weight = quanWeight, text = "Quan")
                        TableCellText(weight = priceWeight, text = "Price")
                    }
                }
                items(assortment) { model ->
                    Row(modifier = Modifier.clickable {
                        changedProducts.add(
                            Purchase(
                                model.article,
                                model.productName,
                                1.0,
                                model.amount
                            )
                        )
                    }) {
                        TableCellText(weight = articleWeight, text = model.article.toString())
                        TableCellText(weight = productNameWeight, text = model.productName)
                        TableCellText(weight = amountWeight, text = model.amount.toString())
                        TableCellText(weight = quanWeight, text = model.quan)
                        TableCellText(weight = priceWeight, text = model.price.toString())
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.Center) {
                Icon(
                    modifier = Modifier.padding(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_downward_24),
                    contentDescription = "arrow"
                )
            }

            LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
                items(changedProducts) { model ->
                    Row(modifier = Modifier.clickable {
                        changedProducts.removeIf { it.article == model.article }
                    }) {
                        val scope = rememberCoroutineScope()

                        TableCellText(weight = articleWeight, text = model.article.toString())
                        TableCellText(weight = productNameWeight, text = model.productName)
                        TableCell(weight = amountWeight) {
                            Button(onClick = {
                                scope.launch { if (model.amount > 0) model.amount -= 1 }
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_arrow_left_24),
                                    contentDescription = "current amount ${model.amount}" // TODO: redraw when add amount
                                )
                            }
                            Text("${model.amount}")
                            Button(onClick = {
                                scope.launch { if (model.amount + 1 - 0.001 < model.maxAmount) model.amount += 1 }
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_arrow_left_24),
                                    contentDescription = "current amount ${model.amount}" // TODO: redraw when add amount
                                )
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.Center) {
                Button(onClick = {/*todo insert*/ }) {
                    Text(text = "Buy")
                }
            }
        }
    }

    private fun SQLiteDatabase.queryAssortment(storeId: Long): MutableList<Model> {
        val res = mutableListOf<Model>()

        query(
            """
            select  t0.${Table.Accounting.PRODUCT_ARTICLE},
                    t1.${Table.Product.NAME},
                    t0.${Table.Accounting.AMOUNT}, 
                    t1.${Table.Product.QUANTITY_TO_ASSESS},
                    t0.${Table.Accounting.COST}
                from ${Table.Accounting.T_NAME} as t0
                inner join ${Table.Product.T_NAME} as t1 
                on t0.${Table.Accounting.PRODUCT_ARTICLE}=t1.${Table.Product.ARTICLE}
                where ${Table.Accounting.STORE_ID}=$storeId;
            """
        ) { model ->
            if (model == null) return@query

            while (model.moveToNext()) {
                res.add(
                    Model(
                        model.getLong(0),
                        model.getString(1),
                        model.getDouble(2),
                        model.getString(3),
                        model.getDouble(4)
                    )
                )
            }
        }

        return res
    }

    private fun SQLiteDatabase.executeBuy(
        storeId: Long,
        purchases: List<Purchase>,
    ) {
        val res = mutableListOf<Purchase>()
        try {

            beginTransaction()
            val checkId = insert(
                Table.CheckList.T_NAME,
                null,
                ContentValues().apply {
                    put(Table.CheckList.STORE_ID, storeId)
                    put(Table.CheckList.TIME, Calendar.getInstance().time.time)
                }
            )

            purchases.forEach { purchase ->
                insert(
                    Table.Purchase.T_NAME, null,
                    ContentValues().apply {
                        put(Table.Purchase.ID, checkId)
                        put(Table.Purchase.PRODUCT_ARTICLE, purchase.article)
                        put(Table.Purchase.AMOUNT, purchase.amount)
                    })

                update(
                    Table.Accounting.T_NAME,
                    ContentValues().apply {
                        put(
                            Table.Accounting.AMOUNT,
                            getAsDouble(Table.Accounting.AMOUNT) - purchase.amount
                        )
                    },
                    "${Table.Accounting.STORE_ID}=$storeId " +
                            "AND ${Table.Accounting.PRODUCT_ARTICLE}=${purchase.article}",
                    null
                )
            }

            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }
}
















