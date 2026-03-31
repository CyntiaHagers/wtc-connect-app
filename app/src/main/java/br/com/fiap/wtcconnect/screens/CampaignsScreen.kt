package br.com.fiap.wtcconnect.screens.campaigns

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.wtcconnect.R
import br.com.fiap.wtcconnect.ui.theme.WtcCrmTheme

data class Campaign(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val imageUrl: Int?
)

val mockCampaigns = listOf(
    Campaign(1, "Feirão WTC", "Até 40% de desconto para associados", "15-20 OUT", R.drawable.desconto),
    Campaign(2, "WTC Summit 2024", "O maior evento de networking do ano.", "25 NOV", R.drawable.networking),
    Campaign(3, "Happy Hour de Negócios", "Conecte-se com líderes do mercado.", "Toda Sexta", R.drawable.negocios)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreen() {
    val campaigns = remember {
        mutableStateListOf<Campaign>().apply {
            addAll(mockCampaigns)
        }
    }

    var showNewCampaignDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var newDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campanhas Recentes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewCampaignDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nova Campanha")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(campaigns, key = { it.id }) { campaign ->
                CampaignCardExpandable(campaign)
            }
        }
    }

    if (showNewCampaignDialog) {
        AlertDialog(
            onDismissRequest = { showNewCampaignDialog = false },
            title = { Text("Nova Campanha") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newDate,
                        onValueChange = { newDate = it },
                        label = { Text("Data") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newTitle.isNotBlank() && newDescription.isNotBlank() && newDate.isNotBlank()) {
                        val nextId = (campaigns.maxOfOrNull { it.id } ?: 0) + 1
                        campaigns.add(
                            Campaign(
                                id = nextId,
                                title = newTitle.trim(),
                                description = newDescription.trim(),
                                date = newDate.trim(),
                                imageUrl = null
                            )
                        )

                        newTitle = ""
                        newDescription = ""
                        newDate = ""
                        showNewCampaignDialog = false
                    }
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCampaignDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CampaignCardExpandable(campaign: Campaign) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isConfirmed by rememberSaveable { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            campaign.imageUrl?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = campaign.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(campaign.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    campaign.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Data", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        campaign.date,
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (expanded) "Ocultar detalhes" else "Ver detalhes")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            isConfirmed = true
                            showMessage = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isConfirmed) Color(0xFF4CAF50) else Color.Transparent,
                            contentColor = if (isConfirmed) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(if (isConfirmed) "Confirmado" else "Confirmar presença")
                    }
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (campaign.id) {
                            1 -> """
                                O Feirão WTC é a oportunidade perfeita para aproveitar grandes descontos exclusivos.
                                Durante o evento, produtos e serviços estarão com até 40% de desconto.
                                Além das ofertas, é uma excelente chance de fazer networking e conhecer novas empresas.
                                O Feirão ocorre entre os dias 15 e 20 de outubro.
                            """.trimIndent()

                            2 -> """
                                O WTC Summit 2024 reúne os maiores líderes e especialistas do mercado.
                                Um dia inteiro de palestras e workshops sobre inovação e negócios.
                                O evento será em 25 de novembro, com ampla programação de networking.
                            """.trimIndent()

                            3 -> """
                                O Happy Hour de Negócios ocorre toda sexta-feira.
                                Um ambiente descontraído para conhecer novos parceiros e líderes do setor.
                                Perfeito para trocar ideias e fortalecer conexões profissionais.
                            """.trimIndent()

                            else -> "Detalhes da campanha: ${campaign.description}"
                        },
                        fontSize = 14.sp
                    )
                }

                if (showMessage) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text(
                                text = "Presença confirmada!",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampaignsScreenPreview() {
    WtcCrmTheme {
        CampaignsScreen()
    }
}