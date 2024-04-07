package com.example.users.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(val id: Int? = null, val name: String? = null, val email: String, val password: String? = null)

@Serializable
data class UserLogin(val name: String? = null, val password: String? = null)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)

    override val primaryKey = PrimaryKey(id)
}
