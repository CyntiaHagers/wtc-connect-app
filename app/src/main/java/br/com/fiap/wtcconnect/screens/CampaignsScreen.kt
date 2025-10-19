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
    Campaign(1, "Feirão WTC", "Até 40% de desconto para associados", "15-20 OUT", R.drawable.ic_launcher_background),
    Campaign(2, "WTC Summit 2024", "O maior evento de networking do ano.", "25 NOV", R.drawable.ic_launcher_background),
    Campaign(3, "Happy Hour de Negócios", "Conecte-se com líderes do mercado.", "Toda Sexta", R.drawable.ic_launcher_background)
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
                CampaignCard(campaign = campaign)
            }
        }
    }
}

@Composable
fun CampaignCard(campaign: Campaign) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
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
                    Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                        Text("Ver detalhes")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                        Text("Confirmar presença")
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
