package br.com.fiap.wtcconnect.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.wtcconnect.ui.theme.RoyalBlue
import br.com.fiap.wtcconnect.ui.theme.WtcCrmTheme
import br.com.fiap.wtcconnect.ui.theme.White

data class ChatItem(
    val name: String,
    val lastMessage: String,
    val isRead: Boolean,
    val isSentByMe: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen() {
    val chats = listOf(
        ChatItem("Leonardo", "podemos conversar?", isRead = true, isSentByMe = true),
        ChatItem("Raul", "Preciso da sua ajuda", isRead = false, isSentByMe = false),
        ChatItem("Cynthia", "Reunião amanhã às 11:00", isRead = true, isSentByMe = true),
        ChatItem("Ana", "está aí ?", isRead = false, isSentByMe = false),
        ChatItem("Rafaela", "como foi o processo ?", isRead = true, isSentByMe = true),
        ChatItem("Soluções Beta", "Por favor, muito obrigado", isRead = false, isSentByMe = true),
        ChatItem("Tech Delta", "pronto", isRead = true, isSentByMe = true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RoyalBlue,
                    titleContentColor = White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(chats) { chat ->
                ChatListItem(chat)
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(RoyalBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.name.first().toString(),
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = chat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (chat.isSentByMe) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Visto",
                        tint = if (chat.isRead) RoyalBlue else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = chat.lastMessage,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        if (!chat.isRead) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(RoyalBlue)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListPreview() {
    WtcCrmTheme {
        ChatListScreen()
    }
}


