package com.zharguy.infinitepodcast.configuration

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("groq")
class GroqConfiguration {
    var apiKey: String? = null
}
