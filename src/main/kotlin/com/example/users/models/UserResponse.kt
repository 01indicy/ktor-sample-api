package com.example.users.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(val user: User?, val message: String?, val status: Int)
