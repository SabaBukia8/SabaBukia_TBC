package com.example.sababukia_tbc

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _users = MutableLiveData<MutableList<User>>(mutableListOf())
    val users: LiveData<MutableList<User>> get() = _users

    private val _snackbarMessage = MutableLiveData<Event<Pair<String, Boolean>>>()
    val snackbarMessage: LiveData<Event<Pair<String, Boolean>>> get() = _snackbarMessage

    fun addUser(user: User) {
        val currentUsers = _users.value ?: mutableListOf()
        if (currentUsers.any { it.email.equals(user.email, ignoreCase = true) }) {
            _snackbarMessage.value = Event(
                Pair(
                    getApplication<Application>().getString(R.string.user_with_this_email_already_exists),
                    false
                )
            )
        } else {
            currentUsers.add(user)
            _users.value = currentUsers
            _snackbarMessage.value = Event(
                Pair(
                    getApplication<Application>().getString(R.string.user_added_successfully),
                    true
                )
            )
        }
    }

    fun updateUser(user: User) {
        val currentUsers = _users.value ?: mutableListOf()
        val index = currentUsers.indexOfFirst { it.email.equals(user.email, ignoreCase = true) }
        if (index != -1) {
            currentUsers[index] = user
            _users.value = currentUsers
            _snackbarMessage.value = Event(
                Pair(
                    getApplication<Application>().getString(R.string.user_updated_successfully),
                    true
                )
            )
        } else {
            _snackbarMessage.value = Event(
                Pair(
                    getApplication<Application>().getString(R.string.user_does_not_exist),
                    false
                )
            )
        }
    }

    fun removeUser(user: User) {
        val currentUsers = _users.value ?: mutableListOf()
        val removed = currentUsers.removeIf { it.email.equals(user.email, ignoreCase = true) }
        if (removed) {
            _users.value = currentUsers
            _snackbarMessage.value = Event(
                Pair(
                    getApplication<Application>().getString(R.string.user_deleted_successfully),
                    true
                )
            )
        } else {
            _snackbarMessage.value = Event(
                Pair(
                    getApplication<Application>().getString(R.string.user_does_not_exist),
                    false
                )
            )
        }
    }
}

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}