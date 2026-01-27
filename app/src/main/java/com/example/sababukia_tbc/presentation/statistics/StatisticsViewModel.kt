package com.example.sababukia_tbc.presentation.statistics

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.WorkspaceError
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.domain.usecase.GetWorkspacesUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsState(
    val workspaces: List<WorkspaceItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface StatisticsEvent {
    data object LoadWorkspaces : StatisticsEvent
}

sealed interface StatisticsSideEffect {
    data class ShowError(val message: String) : StatisticsSideEffect
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getWorkspacesUseCase: GetWorkspacesUseCase
) : BaseViewModel<StatisticsState, StatisticsEvent, StatisticsSideEffect>(
    initialState = StatisticsState()
) {

    init {
        loadWorkspaces()
    }

    override fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.LoadWorkspaces -> loadWorkspaces()
        }
    }

    private fun loadWorkspaces() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            when (val result = getWorkspacesUseCase()) {
                is Result.Success -> {
                    updateState {
                        copy(
                            workspaces = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    val message = when (result.error) {
                        is WorkspaceError.Network -> "Network error. Please check your connection."
                        is WorkspaceError.Unknown -> result.error.message
                    }
                    updateState { copy(isLoading = false, error = message) }
                    emitSideEffect(StatisticsSideEffect.ShowError(message))
                }
            }
        }
    }
}
