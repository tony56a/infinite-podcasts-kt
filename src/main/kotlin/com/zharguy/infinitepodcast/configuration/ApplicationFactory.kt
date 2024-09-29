package com.zharguy.infinitepodcast.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariDataSource
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.codec.RedisCodec
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import java.time.Duration
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
    fun objectMapperProvider(): ObjectMapper =
        jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)

    @Singleton
    @Replaces(RedisCodec::class)
    fun redisCodecProvider(): RedisCodec<ByteArray, ByteArray> {
        return ByteArrayCodec.INSTANCE
    }

    @Singleton
    fun bucketProxyManagerProvider(
        connection: StatefulRedisConnection<ByteArray, ByteArray>
    ) = Bucket4jLettuce.casBasedBuilder(connection).expirationAfterWrite(
        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
            Duration.ofMinutes(
                30
            )
        )
    ).build()

    @Named("default-rate-limit-policy")
    @Singleton
    fun defaultRateLimitPolicyProvider(): BucketConfiguration = BucketConfiguration.builder().addLimit { limit ->
        limit.capacity(6).refillGreedy(6, Duration.ofMinutes(1))
    }.build()


}