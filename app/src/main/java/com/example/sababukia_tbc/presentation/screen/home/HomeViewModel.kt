package com.example.sababukia_tbc.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var loadUsersJob: Job? = null

    init {
        loadUsers()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnProfileClicked -> onProfileClicked()
            is HomeEvent.OnRetry -> loadUsers()
        }
    }

    private fun loadUsers() {
        loadUsersJob?.cancel()
        loadUsersJob = viewModelScope.launch {
            _state.value = _state.value.copy(loader = Resource.Loading(isLoading = true))

            try {
                val result = getUsersUseCase(page = 1)

                result
                    .onSuccess { users ->
                        _state.value = _state.value.copy(
                            loader = Resource.Success(data = users)
                        )
                    }
                    .onFailure { exception ->
                        val errorMessage = exception.message ?: "Failed to load users"
                        _state.value = _state.value.copy(
                            loader = Resource.Error(errorMessage = errorMessage)
                        )
                        _sideEffect.emit(HomeSideEffect.ShowError(errorMessage))
                    }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _state.value = _state.value.copy(
                    loader = Resource.Error(errorMessage = errorMessage)
                )
                _sideEffect.emit(HomeSideEffect.ShowError(errorMessage))
            }
        }
    }

    private fun onProfileClicked() {
        viewModelScope.launch {
            _sideEffect.emit(HomeSideEffect.NavigateToProfile)
        }
    }

    override fun onCleared() {
        super.onCleared()
        loadUsersJob?.cancel()
    }
}
