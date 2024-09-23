package com.zharguy.infinitepodcast.configuration

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("groq")
class GroqConfiguration {
    var url: String? = null
    var apiKey: String? = null
}
