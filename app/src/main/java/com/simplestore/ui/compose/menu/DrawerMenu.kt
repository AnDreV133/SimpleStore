package com.simplestore.ui.compose.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplestore.ui.compose.StateManager

@Composable
fun DrawerMenu(
    menuItems: List<Pair<String, StateManager.State>>,
    state: MutableState<StateManager.State>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        contentPadding = PaddingValues(0.dp, 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(menuItems) { (title, navigationToState) ->
            DrawerMenuItem(title, navigationToState, state)
        }
    }
}

@Composable
fun DrawerMenuItem(
    title: String,
    navigationToState: StateManager.State,
    state: MutableState<StateManager.State>
) {
    Button(onClick = { state.value = navigationToState }) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            softWrap = true,
        )
    }
}