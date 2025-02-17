package com.zharguy.infinitepodcast.events

object QueueChannelConstants {
    // Service script generation job queue
    const val SVC_EXCHANGE_NAME = "script_svc_exchange"
    const val SCRIPT_PROCESSING_QUEUE_NAME = "script_processing.queue"
    const val SCRIPT_PROCESSING_QUEUE_KEY = "script_processing"

    // Front End/Display script queue
    const val FRONT_END_EXCHANGE_NAME = "fe_exchange"
    const val SCRIPT_DISPLAY_QUEUE_NAME = "script_display.queue"
    const val SCRIPT_DISPLAY_QUEUE_KEY = "script_display"
    const val PRIORITY_SCRIPT_DISPLAY_QUEUE_NAME = "priority_script_display.queue"
    const val PRIORITY_SCRIPT_DISPLAY_QUEUE_KEY = "priority_script_display"

    // Requester Notification queue
    const val REQUESTER_EXCHANGE_NAME = "requester_exchange"
    const val SCRIPT_STATUS_QUEUE_NAME = "script_status.queue"
    const val SCRIPT_STATUS_QUEUE_KEY = "script_status"
}