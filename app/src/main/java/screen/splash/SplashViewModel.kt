package screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.usecase.GetAuthTokenUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashDestination {
    Home,
    Welcome
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAuthTokenUseCase: GetAuthTokenUseCase
) : ViewModel() {

    private val _navigationState = MutableStateFlow<SplashDestination?>(null)
    val navigationState: StateFlow<SplashDestination?> = _navigationState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Small delay for splash screen effect
            delay(1500)

            val token = getAuthTokenUseCase().first()
            _navigationState.value = if (!token.isNullOrEmpty()) {
                SplashDestination.Home
            } else {
                SplashDestination.Welcome
            }
        }
    }
}
