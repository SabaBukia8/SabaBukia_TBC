package com.example.sababukia_tbc.presentation.screen.users

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.sababukia_tbc.domain.usecase.GetUsersUseCase
import com.example.sababukia_tbc.presentation.base.BaseViewModel
import com.example.sababukia_tbc.presentation.screen.users.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : BaseViewModel<UsersState, UsersEvent, UsersSideEffect>(
    initialState = UsersState()
) {

    val users = Pager(
        config = PagingConfig(
            pageSize = 6,
            enablePlaceholders = false,
            initialLoadSize = 6
        ),
        pagingSourceFactory = { UserPagingSource(getUsersUseCase) }
    ).flow
        .map { pagingData -> pagingData.map { it.toUiModel() } }
        .cachedIn(viewModelScope)

    override fun onEvent(event: UsersEvent) {
        when (event) {
            is UsersEvent.OnUserClick -> onUserClick(event.userId)
        }
    }

    private fun onUserClick(userId: Int) {
        sendSideEffect(UsersSideEffect.ShowUserDetails(userId))
    }
}
