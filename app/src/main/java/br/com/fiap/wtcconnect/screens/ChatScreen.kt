package br.com.fiap.wtcconnect.screens.chat

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

// Mock Data
data class Message(
    val id: Int,
    val text: String,
    val isFromMe: Boolean,
    val author: String,
    val interactiveData: InteractiveMessage? = null
)

data class InteractiveMessage(
    val title: String,
    val body: String,
    val actions: List<Pair<String, String>>
)

val mockMessages = listOf(
    Message(1, "Olá! Bem-vindo ao WTC.", false, "Atendente"),
    Message(2, "Olá, obrigado!", true, "Eu"),
    Message(
        3, "", false, "Sistema",
        interactiveData = InteractiveMessage(
            title = "🏷️ Campanha Especial",
            body = "Participe do nosso evento exclusivo! Toque para mais detalhes.",
            actions = listOf("Inscrever-se" to "INS", "Saiba mais" to "INFO")
        )
    ),
    Message(4, "Que legal! Vou ver.", true, "Eu"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atendimento WTC") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            MessageInput(
                value = messageText,
                onValueChange = { messageText = it },
                onSend = {
                    // TODO: Lógica de envio de mensagem
                    messageText = ""
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            reverseLayout = true
        ) {
            items(mockMessages.reversed()) { message ->
                MessageBubble(message = message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    var offsetX by remember { mutableStateOf(0f) }

    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (message.isFromMe) RoyalBlue else White
    val textColor = if (message.isFromMe) White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isFromMe) 64.dp else 0.dp,
                end = if (message.isFromMe) 0.dp else 64.dp
            ),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .offset(x = (offsetX / 2).dp) // Efeito visual do swipe
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // TODO: Implementar ação com base no swipe
                            if (offsetX > 150) println("Ação: Marcar como importante")
                            if (offsetX < -150) println("Ação: Criar tarefa")
                            offsetX = 0f // Reset
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                    }
                }
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            if (message.interactiveData != null) {
                InteractiveMessageContent(message.interactiveData)
            } else {
                Text(text = message.text, color = textColor)
            }
        }
    }
}

@Composable
fun InteractiveMessageContent(data: InteractiveMessage) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(data.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(data.body)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                data.actions.forEach { (title, _) ->
                    Button(
                        onClick = { /* TODO: Ação do botão */ },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(title)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Digite uma mensagem ou /comando") },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onSend) {
            Icon(Icons.Default.Send, contentDescription = "Enviar")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    WtcCrmTheme {
        ChatScreen()
    }
}
