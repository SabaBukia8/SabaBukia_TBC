package com.example.mtgcollectionmanager.presentation.util

import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.domain.common.AppError

/**
 * Extension function to convert AppError to UiText for presentation
 */
fun AppError.toUiText(): UiText {
    return when (this) {
        // Auth errors
        AppError.Auth.UserNotLoggedIn -> UiText.StringResource(R.string.error_not_logged_in)
        AppError.Auth.LoginFailed -> UiText.StringResource(R.string.error_login_failed)
        AppError.Auth.RegistrationFailed -> UiText.StringResource(R.string.error_registration_failed)
        AppError.Auth.InvalidCredentials -> UiText.StringResource(R.string.error_invalid_credentials)
        AppError.Auth.WeakPassword -> UiText.StringResource(R.string.error_weak_password)
        AppError.Auth.EmailAlreadyInUse -> UiText.StringResource(R.string.error_email_in_use)
        AppError.Auth.PasswordChangeFailed -> UiText.StringResource(R.string.error_password_change_failed)
        
        // Network errors
        AppError.Network.NoConnection -> UiText.StringResource(R.string.error_no_connection)
        AppError.Network.Timeout -> UiText.StringResource(R.string.error_timeout)
        AppError.Network.ServerError -> UiText.StringResource(R.string.error_server_error)
        
        // Profile errors
        AppError.Profile.LoadFailed -> UiText.StringResource(R.string.error_profile_load_failed)
        AppError.Profile.UpdateFailed -> UiText.StringResource(R.string.error_profile_update_failed)
        AppError.Profile.DeleteFailed -> UiText.StringResource(R.string.error_profile_delete_failed)
        AppError.Profile.NicknameUpdateFailed -> UiText.StringResource(R.string.error_nickname_update_failed)
        
        // Collection errors
        AppError.Collection.LoadFailed -> UiText.StringResource(R.string.error_collection_load_failed)
        AppError.Collection.CreateFailed -> UiText.StringResource(R.string.error_collection_create_failed)
        AppError.Collection.UpdateFailed -> UiText.StringResource(R.string.error_collection_update_failed)
        AppError.Collection.DeleteFailed -> UiText.StringResource(R.string.error_collection_delete_failed)
        
        // Card errors
        AppError.Card.LoadFailed -> UiText.StringResource(R.string.error_card_load_failed)
        AppError.Card.SearchFailed -> UiText.StringResource(R.string.error_card_search_failed)
        AppError.Card.AddFailed -> UiText.StringResource(R.string.error_card_add_failed)
        AppError.Card.RemoveFailed -> UiText.StringResource(R.string.error_card_remove_failed)
        AppError.Card.UpdateFailed -> UiText.StringResource(R.string.error_card_update_failed)
        AppError.Card.NotFound -> UiText.StringResource(R.string.error_card_not_found)
        
        // Category errors
        AppError.Category.LoadFailed -> UiText.StringResource(R.string.error_category_load_failed)
        AppError.Category.CreateFailed -> UiText.StringResource(R.string.error_category_create_failed)
        AppError.Category.UpdateFailed -> UiText.StringResource(R.string.error_category_update_failed)
        AppError.Category.DeleteFailed -> UiText.StringResource(R.string.error_category_delete_failed)
        AppError.Category.MoveCardFailed -> UiText.StringResource(R.string.error_move_card_failed)
        
        // Unknown error
        is AppError.Unknown -> {
            val message = this.message
            if (message.isNullOrBlank()) {
                UiText.StringResource(R.string.error_unknown)
            } else {
                UiText.DynamicString(message)
            }
        }
    }
}