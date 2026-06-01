package com.easypark.app.core.work

object RemoteAbTestKeys {
    const val ENABLED = "ab_testing_enabled"
    const val GROUP = "ab_testing_group"
    const val GROUP_A_FREQUENCY_MINUTES = "ab_sync_frequency_group_a_minutes"
    const val GROUP_B_FREQUENCY_MINUTES = "ab_sync_frequency_group_b_minutes"
    const val GROUP_A_NOTIFICATION = "ab_notification_group_a"
    const val GROUP_B_NOTIFICATION = "ab_notification_group_b"

    const val GROUP_A = "A"
    const val GROUP_B = "B"

    const val DEFAULT_GROUP_A_FREQUENCY_MINUTES = 15L
    const val DEFAULT_GROUP_B_FREQUENCY_MINUTES = 30L
}
