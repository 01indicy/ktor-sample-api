package com.example.users.routes

import com.example.users.models.User
import com.example.users.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {
    route("/users") {
        get {
            call.respond(userService.getAllUsers())
        }
        post {
            val user = call.receive<User>()

            if (user.name.isNullOrBlank()) call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Name field is missing or empty")
            )
            if (user.password.isNullOrBlank()) call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Password field is missing or empty")
            )

            val createdUser = userService.createUser(user)
            call.respond(
                if (createdUser.status == 201) HttpStatusCode.Created else HttpStatusCode.Conflict,
                createdUser
            )
        }
        get("/{id}") { _: Unit ->
            val id: Int = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid id format")
            );
            val user: User? = userService.getUserById(id.toString())

            if (user == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User ID $id not found"))
            } else {
                call.respond(HttpStatusCode.OK, user)
            }
        }
        put("/{id}") { _: Unit ->
            val id: Int = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid id format")
            )
            val user: User? = call.receiveOrNull<User>()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user data"))
            } else {
                val updatedUser: User? = userService.updateUser(user.copy(id = id))
                if (updatedUser == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User ID $id not found"))
                } else {
                    call.respond(HttpStatusCode.OK, updatedUser)
                }
            }
        }
        delete {
            val id: Int = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid id format")
            )
            val deleted: Boolean = userService.deleteUser(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User ID $id not found"))
            }
        }
    }
}