package domain.usecase

import data.DatastoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthTokenUseCase @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    operator fun invoke(): Flow<String?> {
        return datastoreManager.authToken
    }
}
