package com.zharguy.infinitepodcast.handlers

import io.github.oshai.kotlinlogging.KotlinLogging
import io.grpc.*
import jakarta.inject.Singleton

private val logger = KotlinLogging.logger {}

@Singleton
class ExceptionInterceptor : ServerInterceptor {

    private class LoggingServerCallListener<ReqT>(
        delegate: ServerCall.Listener<ReqT>
    ) : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {

        override fun onMessage(message: ReqT) {
            try {
                super.onMessage(message)
            } catch (t: Throwable) {
                logger.error(t) { "error handling message" }
                throw t
            }
        }

    }

    private class ExceptionTranslatingServerCall<ReqT, RespT>(
        delegate: ServerCall<ReqT, RespT>
    ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {

        override fun close(status: Status, trailers: Metadata) {
            if (status.isOk) {
                return super.close(status, trailers)
            }
            val cause = status.cause
            var newStatus = status

            logger.error("Error handling gRPC endpoint.", cause)

            if (status.code == Status.Code.UNKNOWN) {
                val translatedStatus = when (cause) {
                    is IllegalArgumentException -> Status.INVALID_ARGUMENT
                    is IllegalStateException -> Status.FAILED_PRECONDITION
                    is HandlerExceptions.RateLimitedException -> Status.UNAVAILABLE
                    is RuntimeException -> Status.INTERNAL
                    else -> Status.UNKNOWN
                }
                newStatus = translatedStatus.withDescription(cause?.message).withCause(cause)
            }

            super.close(newStatus, trailers)
        }
    }

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        return next.startCall(ExceptionTranslatingServerCall(call), headers)
    }
}