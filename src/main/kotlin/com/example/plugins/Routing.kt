package com.example.plugins

import com.example.users.routes.userAuthRoutes
import com.example.users.services.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import com.example.users.routes.userRoutes

fun Application.configureRouting() {
    routing {
        authenticate("ktor") {
            userRoutes(userService = UserService())
        }
        userAuthRoutes(userService = UserService())
    }
}
