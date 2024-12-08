package com.simplestore.ui.compose

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simplestore.db.Table
import com.simplestore.db.execute
import com.simplestore.db.query
import com.simplestore.ui.compose.table.TableCell
import com.simplestore.ui.compose.table.TableCellText
import com.simplestore.ui.theme.ArrowLeft
import com.simplestore.ui.theme.ArrowRight
import com.simplestore.ui.theme.PathArrowDown
import java.util.Calendar

object Shopping {
    private class Model(
        val article: Long,
        val productName: String,
        val amount: Double,
        val quan: String,
        val price: Double
    )

    class Purchase(
        val article: Long,
        val productName: String,
        var amount: Double,
        val maxAmount: Double
    )

    @Composable
    fun Screen(conn: SQLiteDatabase, storeId: Long, rollback: () -> Unit) {
        val changedProducts = remember { mutableStateListOf<Purchase>() }

        val assortment = conn.queryAssortment(storeId)

        val articleWeight = 0.1f
        val productNameWeight = 0.4f
        val amountWeight = 0.18f
        val quanWeight = 0.16f
        val priceWeight = 0.16f
        Column(modifier = Modifier.fillMaxSize()) {
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
                    Row(modifier = Modifier.clickable {
                        if (changedProducts.none { it.article == model.article })
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    imageVector = Icons.Default.PathArrowDown,
                    contentDescription = "arrow"
                )
            }

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
                                imageVector = Icons.Default.ArrowLeft,
                                contentDescription = "current amount ${model.amount}"
                            )
                            Text(modifier = Modifier.padding(8.dp, 0.dp), text = "$amount")
                            Icon(
                                modifier = buttonModifier {
                                    if (model.amount + 1 - 0.001 < model.maxAmount) model.amount += 1
                                },
                                imageVector = Icons.Default.ArrowRight,
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
                        conn.executeBuy(storeId, changedProducts)
                        rollback()
                    }
                }) {
                    Text(text = "Купить")
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
    ): Boolean {
        var res = false
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
                        put(Table.Purchase.CHECK_LIST_ID, checkId)
                        put(Table.Purchase.PRODUCT_ARTICLE, purchase.article)
                        put(Table.Purchase.AMOUNT, purchase.amount)
                    })

                execute(
                    """
                        UPDATE ${Table.Accounting.T_NAME}
                            SET ${Table.Accounting.AMOUNT}=${Table.Accounting.AMOUNT}-${purchase.amount}
                            WHERE ${Table.Accounting.STORE_ID}=$storeId 
                                AND ${Table.Accounting.PRODUCT_ARTICLE}=${purchase.article};
                                
                        """
                )
            }

            res = true
            setTransactionSuccessful()
        } catch (e: SQLiteException) {
            Log.e("ShoppingState", e.toString())
        } finally {
            endTransaction()
        }

        return res
    }
}
















