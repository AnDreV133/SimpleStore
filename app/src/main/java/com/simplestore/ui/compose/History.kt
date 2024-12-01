package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simplestore.db.Table
import com.simplestore.db.query

object History {
    class PurchaseModel(val name: String, val count: Double, val quan: String, val price: Double)
    class PurchaseHistoryModel(val checkId: Long, val purchases: List<PurchaseModel>)

    @Composable
    fun Screen(conn: SQLiteDatabase, storeId: Long) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(conn.queryGetHistory(storeId)) { model ->
                val modifier = Modifier.padding(bottom = 10.dp)
                Text(
                    modifier = modifier,
                    text = "Check Id: ${model.checkId}"
                )
                for (purchase in model.purchases) {
                    Text(
                        modifier = modifier,
                        text = "Name: ${purchase.name}, Count: ${purchase.count} in ${purchase.quan}, Price: ${purchase.price}"
                    )
                }
            }
        }
    }

    private fun SQLiteDatabase.queryGetHistory(storeId: Long): List<PurchaseHistoryModel> {
        val res = mutableListOf<PurchaseHistoryModel>()
        query(
            """
        select  t0.${Table.CheckList.ID},
                t2.${Table.Product.NAME}, 
                t1.${Table.Purchase.AMOUNT},
                t2.${Table.Product.QUANTITY_TO_ASSESS},
                t1.${Table.Purchase.AMOUNT}*t3.${Table.Accounting.COST}
        from ${Table.CheckList.T_NAME} as t0 
        inner join ${Table.Purchase.T_NAME} as t1
        on t0.${Table.CheckList.ID}=t1.${Table.Purchase.CHECK_LIST_ID}
            and t0.${Table.CheckList.STORE_ID}=$storeId
        inner join ${Table.Product.T_NAME} as t2
        on t1.${Table.Purchase.T_NAME}=t2.${Table.Product.ARTICLE}   
        inner join ${Table.Accounting.T_NAME} as t3
        on t1.${Table.Purchase.PRODUCT_ARTICLE}=t3.${Table.Accounting.PRODUCT_ARTICLE}
            and t3.${Table.Accounting.STORE_ID}=$storeId
        order by t0.${Table.CheckList.ID} asc;
        """
        ) { cursor ->
            if (cursor == null) return@query

            val checkListMap = mutableMapOf<Long, List<PurchaseModel>>()

            while (cursor.moveToNext()) {
                checkListMap.compute(
                    cursor.getLong(0)
                ) { k, v ->
                    (v?.toMutableList() ?: mutableListOf())
                        .apply {
                            add(
                                PurchaseModel(
                                    cursor.getString(1),
                                    cursor.getDouble(2),
                                    cursor.getString(3),
                                    cursor.getDouble(4)
                                )
                            )
                        }
                }
            }

            for (checkList in checkListMap) {
                res.add(PurchaseHistoryModel(checkList.key, checkList.value))
            }
        }

        return res
    }
}