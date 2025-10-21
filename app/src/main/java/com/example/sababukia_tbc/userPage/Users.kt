package com.example.sababukia_tbc.userPage

object Users {
    private val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
    }

    fun removeUser(user: User) {
        users.remove(user)
    }

    fun updateUser(user: User) {
        val index = users.indexOfFirst { it.email == user.email }
        if (index != -1) {
            users[index] = user
        }
    }

    fun getUsers(): List<User> = users.toList()
}