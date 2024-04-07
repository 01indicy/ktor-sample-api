package com.example.users.utils

import com.example.users.dao.DBConfig
import org.flywaydb.core.Flyway

class DatabaseMigration(private val dbConfig: DBConfig) {
    fun migrate() {
        val flyway = Flyway.configure()
            .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
            .baselineOnMigrate(true)
            .load()

        flyway.baseline()
        flyway.migrate()
    }
}