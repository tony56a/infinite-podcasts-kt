package com.zharguy.infinitepodcast.configuration

import com.zaxxer.hikari.HikariDataSource
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

@Factory
class ApplicationFactory {

    @Bean
    fun dataSource(dbConfiguration: DatabaseConfiguration): DataSource {
        return HikariDataSource().apply {
            jdbcUrl = "jdbc:postgresql://${dbConfiguration.host}:${dbConfiguration.port}/${dbConfiguration.db}"
            driverClassName = "org.postgresql.Driver"
            username = requireNotNull(dbConfiguration.username)
            password = requireNotNull(dbConfiguration.password)
        }
    }

    @Bean
    fun databaseProvider(dataSource: DataSource): Database {
        return Database.connect(dataSource)
    }

    @Bean
    fun flywayProvider(dataSource: DataSource): Flyway {
        return Flyway.configure().dataSource(dataSource).load()
    }

}