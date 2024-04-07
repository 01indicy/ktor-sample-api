package com.example.users.dao

import com.example.users.models.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

data class DBConfig(val url: String, val user: String, val password: String)

object DatabaseSingleton {
    fun init() {
        val jdbcUrl = "jdbc:mysql://localhost:3306/ktorProj"
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        val database = Database.connect(jdbcUrl, driverClassName, "root", "root")

        transaction(database) { SchemaUtils.create(Users) }

        suspend fun <T> dbQuery(block: () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}

object Config {
    fun dbConfig(): DBConfig {
        val dbUrl = "jdbc:mysql://localhost:3306/ktorProj"
        val user = "root"
        val password = "root"
        return DBConfig(dbUrl, user, password)
    }
}