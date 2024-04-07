package com.example

import com.example.plugins.*
import com.example.users.dao.Config
import com.example.users.dao.DatabaseSingleton
import com.example.users.utils.DatabaseMigration
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val dbConfig = Config.dbConfig()
    val databaseMigration = DatabaseMigration(dbConfig)

    DatabaseSingleton.init()
    databaseMigration.migrate()
    configureSerialization()
    install(Authentication) {
        basic("ktor") {
            realm = "ktor"
            validate { credentials ->
                if (credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    configureRouting()
}
