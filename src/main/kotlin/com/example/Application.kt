package com.example

import com.example.plugins.*
import com.example.users.dao.DatabaseSingleton
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseSingleton.init()
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
