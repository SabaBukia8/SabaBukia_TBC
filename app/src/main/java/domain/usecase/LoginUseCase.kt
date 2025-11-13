package domain.usecase

import data.DatastoreManager
import kotlinx.coroutines.flow.first
import model.LoginRequest
import repository.AuthRepository
import repository.AuthResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val datastoreManager: DatastoreManager
) {
    suspend operator fun invoke(username: String, password: String): AuthResult<String> {
        // First check local credentials (prioritize locally registered users)
        val savedUsername = datastoreManager.registeredUsername.first()
        val savedPassword = datastoreManager.registeredPassword.first()

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            if (username.equals(savedUsername, ignoreCase = true) && savedPassword == password) {
                // Local login successful - generate a mock token
                val mockToken = "local_token_${System.currentTimeMillis()}"
                datastoreManager.saveAuthToken(mockToken, username)
                datastoreManager.saveRegisteredCredentials(username, datastoreManager.registeredEmail.first() ?: "", password)
                return AuthResult.Success(mockToken)
            } else if (username.equals(savedUsername, ignoreCase = true)) {
                return AuthResult.Error("Incorrect password for user $username")
            }
        }

        // Fallback to API for test users or external accounts
        return when (val result = authRepository.login(LoginRequest(username, password))) {
            is AuthResult.Success -> {
                // Save token from API
                datastoreManager.saveAuthToken(result.data.token, username)
                AuthResult.Success(result.data.token)
            }
            is AuthResult.Error -> result
            is AuthResult.Loading -> result
        }
    }
}
