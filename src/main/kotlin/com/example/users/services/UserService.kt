package com.example.users.services

import com.example.users.dao.UserDao
import com.example.users.models.User
import com.example.users.models.UserResponse
import com.example.users.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

class UserService : UserDao {
    private fun resultRowToUsers(row: ResultRow) = User(
        id = row[Users.id],
        name = row[Users.name],
        email = row[Users.email],
        password = row[Users.password],
    )

    override fun getAllUsers(): List<User> {
        return transaction { Users.selectAll().map(::resultRowToUsers) }
    }

    override fun createUser(user: User): UserResponse {
        return transaction {
            val userExists = Users.select { Users.email eq user.email }.singleOrNull()
            if (userExists != null) {
                return@transaction UserResponse(null, "An user with this email is already registered.", 409)
            }
            val insertUser = Users.insert {
                it[name] = user.name ?: "normal"
                it[email] = user.email
                it[password] = hashPassword(user.password ?: "1234")
            }
            val newUser = insertUser.resultedValues?.singleOrNull()?.let(::resultRowToUsers)!!
            return@transaction UserResponse(newUser, "User Created Successfully", 201)
        }
    }

    override fun getUserById(id: String): User? {
        return transaction {
            Users.select { Users.id eq id.toInt() }
                .mapNotNull(::resultRowToUsers)
                .singleOrNull()
        }
    }

    override fun updateUser(user: User): User? {
        return transaction {
            Users.update({ Users.id eq user.id!! }) {
                it[name] = user.name ?: "normal"
                it[email] = user.email
                it[password] = user.password ?: "1234"
            }
            getUserById(user.id.toString())
        }
    }

    override fun deleteUser(id: Int): Boolean {
        return transaction {
            Users.deleteWhere { Users.id eq id }
            true
        }
    }

    override fun userLogin(name: String, password: String): String? {
        return transaction {
            Users.select { Users.name eq name }
                .singleOrNull()
                ?.let {
                    if (BCrypt.checkpw(password, it[Users.password])) name.createJWT() else null
                }
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun Any.createJWT(): String? {
        val key = io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS512)
        val jwt = Jwts.builder()
            .setSubject(this.toString())
            .setExpiration(Date(System.currentTimeMillis() + 3600 * 24))
            .signWith(key)
            .compact()
        return jwt
    }
}