package com.example.sababukia_tbc.presentation.screen.userlist

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.data.util.ConnectivityMonitor
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.usecase.GetUserListUseCase
import com.example.sababukia_tbc.domain.usecase.RefreshUserListUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import com.example.sababukia_tbc.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUserListUseCase: GetUserListUseCase,
    private val refreshUserListUseCase: RefreshUserListUseCase,
    private val connectivityMonitor: ConnectivityMonitor
) : BaseViewModel<UserListState, UserListEvent, UserListSideEffect>(
    initialState = UserListState()
) {

    init {
        observeUsers()
        observeConnectivity()
    }

    private fun observeUsers() {
        viewModelScope.launch {
            getUserListUseCase()
                .catch { exception ->
                    val errorMessage = exception.message?.let {
                        UiText.DynamicString(it)
                    } ?: UiText.StringResource(R.string.error_unknown)
                    emitSideEffect(UserListSideEffect.ShowError(errorMessage))
                }
                .collect { users ->
                    updateState { it.copy(users = users) }
                }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            combine(
                connectivityMonitor.isOnline,
                getUserListUseCase()
            ) { isOnline, users ->
                Pair(isOnline, users)
            }.collect { (isOnline, users) ->
                updateState {
                    it.copy(
                        isOnline = isOnline,
                        users = users
                    )
                }
            }
        }
    }

    override fun onEvent(event: UserListEvent) {
        when (event) {
            is UserListEvent.Refresh -> refreshUsers()
        }
    }

    private fun refreshUsers() {
        viewModelScope.launch {
            refreshUserListUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoadingFromServer = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        // Data is already in DB, will be reflected via Flow
                        // Loading will be set to false by Loading(false) emission
                    }
                    is Resource.Error -> {
                        emitSideEffect(
                            UserListSideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            )
                        )
                    }
                }
            }
        }
    }
}
