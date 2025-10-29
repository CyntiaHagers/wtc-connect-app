package br.com.fiap.wtcconnect.screens.groups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcconnect.ui.theme.WtcCrmTheme

private val mockGroups = listOf(
    "Associados",
    "Eventos",
    "Parcerias",
    "Comercial",
    "Networking",
    "Novidades"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grupos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(mockGroups) { groupName ->
                GroupItem(name = groupName)
            }
        }
    }
}

@Composable
private fun GroupItem(name: String) {
    Card(
        modifier = Modifier
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupsScreenPreview() {
    WtcCrmTheme {
        GroupsScreen()
    }
}

