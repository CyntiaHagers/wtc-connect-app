package br.com.fiap.wtcconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userType: UserType = UserType.CLIENT,
    val userId: String? = null,
    val userEmail: String? = null,
    val errorMessage: String? = null
)

enum class UserType {
    OPERATOR, CLIENT
}

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _authState.value = _authState.value.copy(
                isAuthenticated = true,
                userId = currentUser.uid,
                userEmail = currentUser.email
            )
        }
    }

    fun login(email: String, password: String, isOperator: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Por favor, preencha todos os campos"
            )
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userType = if (isOperator) UserType.OPERATOR else UserType.CLIENT,
                        userId = user?.uid,
                        userEmail = user?.email,
                        errorMessage = null
                    )
                } else {
                    val errorMessage = when {
                        task.exception?.message?.contains("email address is malformed", ignoreCase = true) == true ->
                            "Formato de e-mail inválido"
                        task.exception?.message?.contains("no user record", ignoreCase = true) == true ->
                            "Usuário não encontrado. Verifique o e-mail"
                        task.exception?.message?.contains("password is invalid", ignoreCase = true) == true ->
                            "Senha incorreta"
                        task.exception?.message?.contains("network", ignoreCase = true) == true ->
                            "Erro de conexão. Verifique sua internet"
                        task.exception?.message?.contains("user is disabled", ignoreCase = true) == true ->
                            "Usuário desabilitado. Contate o suporte"
                        else -> task.exception?.message ?: "Erro ao fazer login"
                    }
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = AuthState()
    }

    fun register(email: String, password: String, confirmPassword: String, isOperator: Boolean) {
        // Validações
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Por favor, preencha todos os campos"
            )
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Formato de e-mail inválido"
            )
            return
        }

        if (password.length < 6) {
            _authState.value = _authState.value.copy(
                errorMessage = "A senha deve ter pelo menos 6 caracteres"
            )
            return
        }

        if (password != confirmPassword) {
            _authState.value = _authState.value.copy(
                errorMessage = "As senhas não coincidem"
            )
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userType = if (isOperator) UserType.OPERATOR else UserType.CLIENT,
                        userId = user?.uid,
                        userEmail = user?.email,
                        errorMessage = null
                    )
                } else {
                    val errorMessage = when {
                        task.exception?.message?.contains("email address is already in use", ignoreCase = true) == true ->
                            "Este e-mail já está cadastrado"
                        task.exception?.message?.contains("weak password", ignoreCase = true) == true ->
                            "Senha muito fraca. Use pelo menos 6 caracteres"
                        task.exception?.message?.contains("network", ignoreCase = true) == true ->
                            "Erro de conexão. Verifique sua internet"
                        else -> task.exception?.message ?: "Erro ao criar conta"
                    }
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }
}
