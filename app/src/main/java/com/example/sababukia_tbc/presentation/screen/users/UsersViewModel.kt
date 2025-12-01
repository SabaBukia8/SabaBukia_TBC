package com.example.sababukia_tbc.presentation.screen.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.sababukia_tbc.domain.usecase.GetUsersUseCase
import com.example.sababukia_tbc.presentation.screen.users.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    val users = getUsersUseCase()
        .map { pagingData -> pagingData.map { it.toUiModel() } }
        .cachedIn(viewModelScope)

    private val _sideEffect = MutableSharedFlow<UsersSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun onEvent(event: UsersEvent) {
        when (event) {
            is UsersEvent.OnUserClick -> onUserClick(event.userId)
        }
    }

    private fun onUserClick(userId: Int) {
        viewModelScope.launch {
            _sideEffect.emit(UsersSideEffect.ShowUserDetails(userId))
        }
    }
}
