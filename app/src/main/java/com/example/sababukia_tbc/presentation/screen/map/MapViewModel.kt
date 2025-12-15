package com.example.sababukia_tbc.presentation.screen.map

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.common.DomainError
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.model.UserLocation
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.usecase.GetLocationsUseCase
import com.example.sababukia_tbc.domain.usecase.GetUserLocationUseCase
import com.example.sababukia_tbc.domain.usecase.SaveLocationUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import com.example.sababukia_tbc.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val saveLocationUseCase: SaveLocationUseCase
) : BaseViewModel<MapViewModel.ViewState, MapViewModel.Event, MapViewModel.SideEffect>(
    initialState = ViewState(
        locationsState = LocationsState.Loading,
        userLocationState = UserLocationState.Loading
    )
) {

    init {
        fetchLocations()
        getCurrentUserLocation()
    }

    sealed class Event {
        data class SaveLocation(val location: Location) : Event()
        object RefreshLocations : Event()
        object RefreshLocationsManual : Event()
        object GetCurrentLocation : Event()
        object GetCurrentLocationManual : Event()
    }

    sealed class SideEffect {
        data class ShowError(val message: UiText) : SideEffect()
        data class ShowSuccess(val message: UiText) : SideEffect()
    }

    data class ViewState(
        val locationsState: LocationsState,
        val userLocationState: UserLocationState
    )

    override fun onEvent(event: Event) {
        when (event) {
            is Event.SaveLocation -> saveLocation(event.location)
            is Event.RefreshLocations -> fetchLocations(manual = false)
            is Event.RefreshLocationsManual -> fetchLocations(manual = true)
            is Event.GetCurrentLocation -> getCurrentUserLocation(manual = false)
            is Event.GetCurrentLocationManual -> getCurrentUserLocation(manual = true)
        }
    }

    private fun fetchLocations(manual: Boolean = false) {
        viewModelScope.launch {
            getLocationsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        updateState { currentState ->
                            currentState.copy(
                                locationsState = LocationsState.Success(
                                    result.data ?: emptyList()
                                )
                            )
                        }
                    }

                    is Resource.Error -> {
                        val errorMessage = getErrorMessage(result.error)
                        updateState { currentState ->
                            currentState.copy(locationsState = LocationsState.Error(errorMessage))
                        }
                        if (manual) emitSideEffect(SideEffect.ShowError(errorMessage))
                    }

                    is Resource.Loading -> {
                        updateState { currentState ->
                            currentState.copy(locationsState = LocationsState.Loading)
                        }
                    }
                }
            }
        }
    }

    private fun getErrorMessage(error: DomainError): UiText {
        return when (error) {
            DomainError.LocationServicesUnavailable ->
                UiText.StringResource(R.string.error_location_unavailable)

            DomainError.LocationPermissionDenied ->
                UiText.StringResource(R.string.location_permission_denied)

            DomainError.DatabaseError ->
                UiText.StringResource(R.string.error_database)

            DomainError.ItemNotFound ->
                UiText.StringResource(R.string.error_item_not_found)

            DomainError.NetworkError ->
                UiText.StringResource(R.string.error_network)

            is DomainError.GeneralError ->
                error.throwable?.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.error_general)

            DomainError.UnknownError ->
                UiText.StringResource(R.string.error_unknown)
        }
    }

    private fun getCurrentUserLocation(manual: Boolean = false) {
        viewModelScope.launch {
            getUserLocationUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            updateState { currentState ->
                                currentState.copy(userLocationState = UserLocationState.Success(it))
                            }
                        }
                    }

                    is Resource.Error -> {
                        val errorMessage = getErrorMessage(result.error)
                        updateState { currentState ->
                            currentState.copy(
                                userLocationState = UserLocationState.Error(
                                    errorMessage
                                )
                            )
                        }
                        if (manual) emitSideEffect(SideEffect.ShowError(errorMessage))
                    }

                    is Resource.Loading -> {
                        updateState { currentState ->
                            currentState.copy(userLocationState = UserLocationState.Loading)
                        }
                    }
                }
            }
        }
    }

    private fun saveLocation(location: Location) {
        viewModelScope.launch {
            saveLocationUseCase(location).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        fetchLocations()
                        emitSideEffect(
                            SideEffect.ShowSuccess(
                                UiText.StringResource(R.string.location_saved_success)
                            )
                        )
                    }

                    is Resource.Error -> {
                        emitSideEffect(SideEffect.ShowError(getErrorMessage(result.error)))
                    }

                    else -> {
                    }
                }
            }
        }
    }

    sealed class LocationsState {
        object Loading : LocationsState()
        data class Success(val locations: List<Location>) : LocationsState()
        data class Error(val message: UiText) : LocationsState()
    }

    sealed class UserLocationState {
        object Loading : UserLocationState()
        data class Success(val location: UserLocation) : UserLocationState()
        data class Error(val message: UiText) : UserLocationState()
    }
}
