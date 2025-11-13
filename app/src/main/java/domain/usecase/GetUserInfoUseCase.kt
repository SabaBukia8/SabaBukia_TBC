package domain.usecase

import data.DatastoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class UserInfo(
    val username: String?,
    val email: String?,
    val userId: String?
)

class GetUserInfoUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    operator fun invoke(): Flow<UserInfo> {
        return combine(
            datastoreManager.registeredUsername,
            datastoreManager.registeredEmail,
            datastoreManager.userId
        ) { username, email, userId ->
            UserInfo(username, email, userId)
        }
    }
}
