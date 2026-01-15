package com.example.sababukia_tbc.presentation.register

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.repository.RegisterRepository
import com.example.sababukia_tbc.domain.util.ValidationError
import com.example.sababukia_tbc.domain.util.ValidationResult
import com.example.sababukia_tbc.domain.util.Validator
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : BaseViewModel<NicknameState, NicknameEvent, NicknameSideEffect>(NicknameState()) {

    override fun onEvent(event: NicknameEvent) {
        when (event) {
            is NicknameEvent.NicknameChanged -> updateState {
                copy(nickname = event.nickname, nicknameError = null)
            }
            NicknameEvent.Submit -> updateNickname()
        }
    }

    private fun updateNickname() {
        val nicknameValidation = Validator.validateNickname(currentState.nickname)
        val nicknameError = (nicknameValidation as? ValidationResult.Invalid)?.errorKey

        if (nicknameError != null) {
            updateState { copy(nicknameError = nicknameError) }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            when (val result = registerRepository.updateDisplayName(currentState.nickname)) {
                is AuthResult.Success -> {
                    sendSideEffect(NicknameSideEffect.ShowMessage(MessageType.SUCCESS))
                    sendSideEffect(NicknameSideEffect.NavigateToHome)
                }
                is AuthResult.Error -> {
                    sendSideEffect(NicknameSideEffect.ShowMessage(MessageType.ERROR, result.message))
                }
            }

            updateState { copy(isLoading = false) }
        }
    }
}

data class NicknameState(
    val nickname: String = "",
    val nicknameError: ValidationError? = null,
    val isLoading: Boolean = false
)

sealed class NicknameEvent {
    data class NicknameChanged(val nickname: String) : NicknameEvent()
    data object Submit : NicknameEvent()
}

sealed class NicknameSideEffect {
    data object NavigateToHome : NicknameSideEffect()
    data class ShowMessage(val type: MessageType, val customMessage: String? = null) : NicknameSideEffect()
}
