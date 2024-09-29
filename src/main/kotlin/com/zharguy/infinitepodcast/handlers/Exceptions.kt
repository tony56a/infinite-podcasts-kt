package com.zharguy.infinitepodcast.handlers

sealed class HandlerExceptions(override val message: String, override val cause: Throwable? = null) : Exception(
    message,
    cause
) {
    class RateLimitedException(methodName: String) : HandlerExceptions("The $methodName request is rate limited")
}

