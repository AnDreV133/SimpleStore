package com.simplestore.ui.compose

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simplestore.db.Table
import com.simplestore.db.query
import com.simplestore.ui.compose.table.TableCellText

object Rating {
    class Model(val name: String, val partInPercent: Double)

    @Composable
    fun Screen(conn: SQLiteDatabase, storeId: Long) {

        val products = conn.executeStatistic(storeId)

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
                    TableCellText(weight = nameWeight, text = model.name)
                    TableCellText(weight = partInPercentWeight, text = "${model.partInPercent}%")
                }
            }
        }
    }

    fun SQLiteDatabase.executeStatistic(storeId: Long): List<Model> {
        val res = mutableListOf<Model>()
        query(
            """
            select  t2.${Table.Product.NAME}, 
                    round(coalesce(sum(t1.${Table.Purchase.AMOUNT})*100/(select sum(amount) from ${Table.Purchase.T_NAME}),0),2) 
                        as amount_in_percent 
                from check_list as t0 
                inner join ${Table.Purchase.T_NAME} as t1
                on t0.id=t1.${Table.Purchase.CHECK_LIST_ID} 
                    and t0.${Table.CheckList.STORE_ID}=$storeId
                right join ${Table.Product.T_NAME} as t2
                on t1.${Table.Purchase.PRODUCT_ARTICLE}=t2.${Table.Product.ARTICLE}
                group by t2.${Table.Product.NAME}
                order by amount_in_percent desc;
            """
        ) { cursor ->
            if (cursor == null) return@query

            while (cursor.moveToNext()) {
                res.add(
                    Model(
                        name = cursor.getString(0),
                        partInPercent = cursor.getDouble(1)
                    )
                )
            }
        }

        return res
    }
}
