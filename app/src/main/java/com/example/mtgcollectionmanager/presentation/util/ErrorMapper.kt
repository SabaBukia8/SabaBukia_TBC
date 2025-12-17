package com.example.mtgcollectionmanager.presentation.util

import com.example.mtgcollectionmanager.domain.common.AppError
import javax.inject.Inject

/**
 * Utility to map domain-level errors to user-friendly messages
 */
class ErrorMapper @Inject constructor() {
    
    /**
     * Maps an AppError to a user-friendly message resource ID
     */
    fun mapToMessage(error: AppError): String {
        return when (error) {
            is AppError.Auth.UserNotLoggedIn -> "You are not logged in. Please sign in to continue."
            is AppError.Auth.LoginFailed -> "Login failed. Please check your credentials and try again."
            is AppError.Auth.RegistrationFailed -> "Registration failed. Please try again."
            is AppError.Auth.InvalidCredentials -> "Invalid email or password."
            is AppError.Auth.WeakPassword -> "Password is too weak. Please use a stronger password."
            is AppError.Auth.EmailAlreadyInUse -> "This email is already in use. Try another one."
            is AppError.Auth.PasswordChangeFailed -> "Failed to change password. Please try again."
            
            is AppError.Network.NoConnection -> "No internet connection. Please check your network settings."
            is AppError.Network.Timeout -> "Request timed out. Please try again."
            is AppError.Network.ServerError -> "Server error occurred. Please try again later."
            
            is AppError.Profile.LoadFailed -> "Failed to load profile. Please try again."
            is AppError.Profile.UpdateFailed -> "Failed to update profile. Please try again."
            is AppError.Profile.DeleteFailed -> "Failed to delete profile. Please try again."
            is AppError.Profile.NicknameUpdateFailed -> "Failed to update nickname. Please try again."
            
            is AppError.Collection.LoadFailed -> "Failed to load collection. Please try again."
            is AppError.Collection.CreateFailed -> "Failed to create collection. Please try again."
            is AppError.Collection.UpdateFailed -> "Failed to update collection. Please try again."
            is AppError.Collection.DeleteFailed -> "Failed to delete collection. Please try again."
            
            is AppError.Card.LoadFailed -> "Failed to load cards. Please try again."
            is AppError.Card.SearchFailed -> "Search failed. Please try different search terms."
            is AppError.Card.AddFailed -> "Failed to add card. Please try again."
            is AppError.Card.RemoveFailed -> "Failed to remove card. Please try again."
            is AppError.Card.UpdateFailed -> "Failed to update card. Please try again."
            is AppError.Card.NotFound -> "Card not found."
            
            is AppError.Category.LoadFailed -> "Failed to load categories. Please try again."
            is AppError.Category.CreateFailed -> "Failed to create category. Please try again."
            is AppError.Category.UpdateFailed -> "Failed to update category. Please try again."
            is AppError.Category.DeleteFailed -> "Failed to delete category. Please try again."
            is AppError.Category.MoveCardFailed -> "Failed to move card. Please try again."
            
            is AppError.Unknown -> error.message ?: "An unknown error occurred. Please try again."
        }
    }
}