package com.example.users.dao

import com.example.users.models.User
import com.example.users.models.UserResponse

interface UserDao {
    fun getAllUsers(): List<User>
    fun createUser(user: User): UserResponse
    fun getUserById(id: String): User?
    fun updateUser(user: User): User?
    fun deleteUser(id: Int): Boolean
    fun userLogin(name: String, password: String): String?
}