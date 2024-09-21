package com.zharguy.infinitepodcast.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariDataSource
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import java.sql.Connection
import javax.sql.DataSource

@Factory
class ApplicationFactory {

    @Singleton
    fun dataSource(dbConfiguration: DatabaseConfiguration): DataSource {
        return HikariDataSource().apply {
            jdbcUrl =
                "jdbc:postgresql://${dbConfiguration.host}:${dbConfiguration.port}/${dbConfiguration.db}?prepareThreshold=0"
            driverClassName = "org.postgresql.Driver"
            username = requireNotNull(dbConfiguration.username)
            password = requireNotNull(dbConfiguration.password)
        }
    }

    @Singleton
    fun databaseProvider(dataSource: DataSource): Database =
        Database.connect(datasource = dataSource, databaseConfig = DatabaseConfig {
            useNestedTransactions = false
        })

    @Singleton
    fun flywayProvider(dataSource: DataSource): Flyway = Flyway.configure().dataSource(dataSource).load()

    @Singleton
    fun objectMapperProvider(): ObjectMapper = jacksonObjectMapper()
}