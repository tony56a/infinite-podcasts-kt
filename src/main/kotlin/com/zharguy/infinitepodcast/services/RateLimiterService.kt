package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.models.ExtUserSource
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton


@Singleton
class RateLimiterService {

    @Inject
    lateinit var proxyManager: LettuceBasedProxyManager<ByteArray>

    @Inject
    @Named("default-rate-limit-policy")
    lateinit var defaultPolicy: BucketConfiguration

    fun checkLimit(userExtId: String, userSource: ExtUserSource, method: String): Boolean {
        if (userSource == ExtUserSource.ADMIN || userSource == ExtUserSource.AUTOMATION) {
            return true
        }
        val bucket: Bucket =
            proxyManager.getProxy(Triple(userExtId, userSource.name, method).toString().toByteArray()) { defaultPolicy }
        return bucket.tryConsume(1)
    }
}