package com.example.sababukia_tbc.presentation.screen.statistics

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.usecase.GetWorkspacesUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import com.example.sababukia_tbc.presentation.mapper.toUi
import com.example.sababukia_tbc.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            getWorkspacesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        updateState {
                            it.copy(
                                workspaces = resource.data.map { workspace -> workspace.toUi() },
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        updateState { it.copy(isLoading = false) }
                        emitSideEffect(
                            StatisticsSideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            )
                        )
                    }
                }
            }
        }
    }
}
