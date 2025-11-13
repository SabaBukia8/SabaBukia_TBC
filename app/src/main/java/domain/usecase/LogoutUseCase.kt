package domain.usecase

import data.DatastoreManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    suspend operator fun invoke() {
        datastoreManager.clearAuthToken()
    }
}
