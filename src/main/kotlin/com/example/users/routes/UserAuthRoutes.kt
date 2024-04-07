package com.example.users.routes

import com.example.users.models.UserLogin
import com.example.users.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userAuthRoutes(userService: UserService) {
    route("/user-auth") {
        post {
            val userLogin = call.receive<UserLogin>().apply {
                if (name.isNullOrBlank()) call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Name field is missing or empty")
                )
                if (password.isNullOrBlank()) call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Password field is missing or empty")
                )
            }

            val isUserAuthenticated = userService.userLogin(userLogin.name!!, userLogin.password!!)
            if (isUserAuthenticated == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                return@post
            }
            call.respond(HttpStatusCode.OK, mapOf("token" to isUserAuthenticated))
//            call.respond(HttpStatusCode.OK, mapOf("message" to "User Authenticated"))
//            call.respondText("User Authenticated")
        }
    }
}