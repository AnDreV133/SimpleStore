package com.simplestore

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplestore.ui.theme.SimpleStoreTheme
import com.simplestore.db.connect
import com.simplestore.ui.compose.MainScreen

class MainActivity : ComponentActivity() {
    private var conn: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        conn = connect(baseContext)

        setContent {
            MainScreen(conn)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        conn?.close()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleStoreTheme {
        Greeting("Android")
    }
}