package br.com.fiap.wtcconnect.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.wtcconnect.ui.theme.AccentGreen
import br.com.fiap.wtcconnect.ui.theme.WtcCrmTheme

data class Client(
    val id: Int,
    val name: String,
    val status: String,
    val tags: List<String>,
    val score: Int
)

val mockClients = listOf(
    Client(1, "Empresa Alpha", "Ativo", listOf("VIP", "Lead Quente"), 95),
    Client(2, "Soluções Beta", "Inativo", listOf("Ex-cliente"), 30),
    Client(3, "Inovações Gamma", "Em prospecção", listOf("Follow-up"), 70),
    Client(4, "Tech Delta", "Ativo", listOf("Contrato Anual"), 88)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Barra de busca e filtros
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar cliente...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                // TODO: Adicionar botões/chips para filtros
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockClients.filter { it.name.contains(searchQuery, ignoreCase = true) }) { client ->
                    ClientListItem(client = client)
                }
            }
        }
    }
}

@Composable
fun ClientListItem(client: Client) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (client.status == "Ativo") AccentGreen else Color.Gray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(client.name, fontWeight = FontWeight.Bold)
                Text(client.status, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Row {
                    client.tags.forEach { tag ->
                        Chip(label = tag)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Message, contentDescription = "Nova Mensagem")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.NoteAdd, contentDescription = "Adicionar Anotação")
            }
        }
    }
}

@Composable
fun Chip(label: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ClientsScreenPreview() {
    WtcCrmTheme {
        ClientsScreen()
    }
}
