package com.zharguy.infinitepodcast.configuration

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("database")
class DatabaseConfiguration {
    var username: String? = null
    var password: String? = null
    var host: String? = null
    var port: Int? = null
    var db: String? = null
}
