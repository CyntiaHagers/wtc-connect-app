package br.com.fiap.wtcconnect.data

// Modelos de domínio simples usados pelo módulo de chat

data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val avatarUrl: String? = null
)

@Suppress("unused")
data class Conversation(
    val id: String,
    val peerUser: User,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int = 0
)

@Suppress("unused")
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val delivered: Boolean = true
)

// Grupo simples que contém id e nome; usuários são associados por id em FakeChatRepository
data class Group(
    val id: String,
    val name: String
)
