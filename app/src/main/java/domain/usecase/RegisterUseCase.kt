package domain.usecase

import data.DatastoreManager
import model.RegisterRequest
import repository.AuthRepository
import repository.AuthResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val datastoreManager: DatastoreManager
) {
    suspend operator fun invoke(username: String, email: String, password: String): AuthResult<String> {
        return when (val result = authRepository.register(RegisterRequest(username, email, password))) {
            is AuthResult.Success -> {
                // Save credentials locally for future login enforcement
                datastoreManager.saveRegisteredCredentials(username, email, password)
                // Save auth token
                datastoreManager.saveAuthToken(result.data.token, result.data.id.toString())
                AuthResult.Success(result.data.token)
            }
            is AuthResult.Error -> result
            is AuthResult.Loading -> result
        }
    }
}
