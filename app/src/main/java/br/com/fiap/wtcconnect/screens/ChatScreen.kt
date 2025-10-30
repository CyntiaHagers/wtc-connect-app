package br.com.fiap.wtcconnect.screens.chat

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.wtcconnect.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.wtcconnect.ui.theme.RoyalBlue
import br.com.fiap.wtcconnect.ui.theme.WtcCrmTheme
import br.com.fiap.wtcconnect.ui.theme.White
import br.com.fiap.wtcconnect.notifications.InAppEventBus
import br.com.fiap.wtcconnect.notifications.InAppEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.ui.window.Dialog
import android.widget.Toast

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatId: String = "default_chat") {
    val factory = ChatViewModel.Factory(chatId)
    val vm: ChatViewModel = viewModel(factory = factory)
    val messages by vm.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showCreateInteractive by remember { mutableStateOf(false) }

    // Observe in-app events (from FCM service) to show popup notifications
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        InAppEventBus.events.collectLatest { event ->
            when (event) {
                is InAppEvent.NewMessage -> {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = event.title ?: "Nova mensagem",
                            actionLabel = "Abrir"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            // scroll to last message
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    val current = FirebaseAuth.getInstance().currentUser
                    if (messageText.isNotBlank() && current != null) {
                        vm.sendMessage(messageText.trim(), current.uid, current.displayName ?: current.email)
                        messageText = ""
                    }
                },
                onCreateInteractive = { showCreateInteractive = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            state = listState,
            reverseLayout = false
        ) {
            items(messages) { msg ->
                // Map ChatMessage to UI Message model for compatibility
                val uiMessage = Message(
                    id = msg.id.hashCode(),
                    text = msg.text,
                    isFromMe = msg.senderId == FirebaseAuth.getInstance().currentUser?.uid,
                    author = msg.senderName ?: "",
                    interactiveData = null
                )
                // If message has interactive payload, render special UI
                if (msg.interactive != null) {
                    val actions = (msg.interactive["actions"] as? List<*>)
                        ?.mapNotNull { item ->
                            val map = item as? Map<*, *>
                            val entry = map?.entries?.firstOrNull()
                            entry?.let { Pair(it.key.toString(), it.value.toString()) }
                        } ?: emptyList()

                    InteractiveMessageContent(
                        InteractiveMessage(
                            title = (msg.interactive["title"] as? String) ?: "",
                            body = (msg.interactive["body"] as? String) ?: "",
                            actions = actions
                        )
                    )
                } else {
                    MessageBubble(message = uiMessage)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showCreateInteractive) {
        CreateInteractiveDialog(
            onDismiss = { showCreateInteractive = false },
            onCreate = { title, body, actions ->
                val current = FirebaseAuth.getInstance().currentUser
                if (current != null) {
                    // Build interactive map
                    val interactiveMap = hashMapOf<String, Any>(
                        "title" to title,
                        "body" to body,
                        "actions" to actions.map { mapOf(it.first to it.second) }
                    )
                    vm.sendInteractiveMessage(body, current.uid, current.displayName ?: current.email, interactiveMap)
                }
                showCreateInteractive = false
            }
        )
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
fun InteractiveMessageContent(interactive: InteractiveMessage) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(interactive.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(interactive.body)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                val context = LocalContext.current
                val currentUser = FirebaseAuth.getInstance().currentUser
                interactive.actions.forEach { (title, payload) ->
                    Button(
                        onClick = {
                            // If payload looks like a URL open browser
                            if (payload.startsWith("http://") || payload.startsWith("https://")) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = android.net.Uri.parse(payload)
                                context.startActivity(intent)
                            } else {
                                // Otherwise, treat as command/payload: fallback simple behavior is showing a toast
                                Toast.makeText(context, "Ação: $payload", Toast.LENGTH_SHORT).show()
                                // TODO: integrate a callback to ViewModel to perform richer action (e.g., send reply or deep link)
                            }
                        },
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
    onSend: () -> Unit,
    onCreateInteractive: () -> Unit = {}
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
        IconButton(onClick = onCreateInteractive) {
            Icon(Icons.Default.Add, contentDescription = "Criar Interativo")
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onSend) {
            Icon(Icons.Default.Send, contentDescription = "Enviar")
        }
    }
}

@Composable
fun CreateInteractiveDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, body: String, actions: List<Pair<String, String>>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var actionTitle by remember { mutableStateOf("") }
    var actionPayload by remember { mutableStateOf("") }
    val actions = remember { mutableStateListOf<Pair<String, String>>() }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Criar Mensagem Interativa", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Corpo") })
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedTextField(value = actionTitle, onValueChange = { actionTitle = it }, label = { Text("Ação título") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(value = actionPayload, onValueChange = { actionPayload = it }, label = { Text("Ação payload") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = {
                        if (actionTitle.isNotBlank() && actionPayload.isNotBlank()) {
                            actions.add(actionTitle to actionPayload)
                            actionTitle = ""
                            actionPayload = ""
                        }
                    }) { Text("Adicionar Ação") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ações: ${actions.size}")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onCreate(title, body, actions.toList()) }) { Text("Enviar") }
                }
            }
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
