package com.simplestore.ui.compose

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplestore.db.execute
import com.simplestore.db.query
import com.simplestore.ui.theme.ContentCopy
import kotlinx.coroutines.launch

object Console {
    @Composable
    fun Screen(conn: SQLiteDatabase) {
        var input by rememberSaveable { mutableStateOf("") }
        var output by remember { mutableStateOf("") }
        var isExecuteSuccess by remember { mutableStateOf<Boolean?>(null) }
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CommandLineTextField(value = input, onValueChange = { input = it })

            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    scope.launch {
                        conn.sendExecuteAndGet(input).fold(
                            onSuccess = {
                                isExecuteSuccess = true
                                output = it
                            },
                            onFailure = {
                                isExecuteSuccess = false
                                output = it.message ?: "Ошибка запроса"
                            }
                        )
                    }
                }
            ) {
                Text("Запросить")
            }

            if (isExecuteSuccess == true) {
                TextInColoredRoundedRect(
                    backgroundColor = Color.Green,
                    textColor = Color.White,
                    text = "Успешно"
                )
            } else if (isExecuteSuccess == false) {
                TextInColoredRoundedRect(
                    backgroundColor = Color.Red,
                    textColor = Color.Black,
                    text = "Ошибка"
                )
            }

            CommandLineTextField(
                value = output,
                onValueChange = {},
                readOnly = true,
                copyButton = true
            )
        }
    }

    @Composable
    fun TextInColoredRoundedRect(
        backgroundColor: Color,
        textColor: Color,
        text: String,
        modifier: Modifier = Modifier
    ) {
        Text(
            modifier = modifier
                .drawWithCache {
                    onDrawBehind {
                        drawRoundRect(
                            color = backgroundColor,
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }
                }
                .padding(8.dp),
            color = textColor,
            text = text
        )
    }

    @Composable
    fun CommandLineTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        readOnly: Boolean = false,
        copyButton: Boolean = false,
    ) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Black)
        ) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    readOnly = readOnly,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    minLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )

                if (copyButton)
                    IconButton(
                        onClick = {
                            copyToClipboard(context, value)
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            tint = Color.White,
                            contentDescription = "copy button"
                        )
                    }
            }
        }
    }

    private fun copyToClipboard(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("command", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Command copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private suspend fun SQLiteDatabase.sendExecuteAndGet(
        sql: String
    ): Result<String> = sql.trim().let {
        try {
            if (it.startsWith("SELECT", true)) {
                val sb = StringBuilder()
                val formatOutput = { list: List<String> ->
                    sb.append("| ")
                    for (item in list)
                        sb.append(item)
                            .append("\t | ")
                    sb.append("\n")
                }
                query(it) { cursor ->
                    if (cursor == null) return@query

                    formatOutput(cursor.columnNames.toList())

                    while (cursor.moveToNext())
                        formatOutput(
                            (0..<cursor.columnCount)
                                .map { i -> cursor.getString(i) }
                        )
                }
                Result.success(sb.toString())
            } else {
                execute(it)
                Result.success("${it.substringBefore(' ')} - executed")
            }
        } catch (e: SQLException) {
            Result.failure(e)
        }
    }
}

