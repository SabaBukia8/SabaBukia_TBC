package screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.usecase.GetUserInfoUseCase
import domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val userId: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            getUserInfoUseCase().collect { userInfo ->
                _uiState.value = _uiState.value.copy(
                    username = userInfo.username,
                    email = userInfo.email,
                    userId = userInfo.userId
                )
                Log.d(TAG, "User info loaded: ${userInfo.username}, ${userInfo.email}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                logoutUseCase()
                Log.d(TAG, "Logout successful")
                _logoutSuccess.value = true
            } catch (e: Exception) {
                Log.e(TAG, "Logout failed", e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
