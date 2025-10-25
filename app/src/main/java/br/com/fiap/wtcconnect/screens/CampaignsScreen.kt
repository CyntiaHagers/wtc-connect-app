package br.com.fiap.wtcconnect.screens.campaigns


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
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
import br.com.fiap.wtcconnect.R // Supondo que as imagens estão em res/drawable
import br.com.fiap.wtcconnect.ui.theme.WtcCrmTheme


data class Campaign(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val imageUrl: Int // Usando Int para ID do drawable
)

val mockCampaigns = listOf(
    Campaign(1, "Feirão WTC", "Até 40% de desconto para associados", "15-20 OUT", R.drawable.desconto),
    Campaign(2, "WTC Summit 2024", "O maior evento de networking do ano.", "25 NOV", R.drawable.networking),
    Campaign(3, "Happy Hour de Negócios", "Conecte-se com líderes do mercado.", "Toda Sexta", R.drawable.negocios)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campanhas Recentes") },
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
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mockCampaigns) { campaign ->
                CampaignCardExpandable(campaign)

            }
        }
    }
}

@Composable
fun CampaignCardExpandable(campaign: Campaign) {
    var expanded by rememberSaveable  { mutableStateOf(false) }
    var isConfirmed by rememberSaveable  { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Image(
                painter = painterResource(id = campaign.imageUrl),
                contentDescription = campaign.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(campaign.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(campaign.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Data", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(campaign.date, color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(onClick = { expanded = !expanded }, modifier = Modifier.weight(1f)) {
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

                // Detalhes do evento
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
                            else -> "Detalhes não disponíveis."
                        },
                        fontSize = 14.sp
                    )
                }

                // Mensagem de confirmação temporária
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
