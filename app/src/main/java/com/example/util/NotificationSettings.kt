package com.example.util

data class NotificationConfig(
    val expirationAlertsEnabled: Boolean = true,
    val expirationDaysThreshold: Int = 30, // alert if product expires within X days
    val lowStockAlertsEnabled: Boolean = true,
    val lowStockThreshold: Int = 5, // minimum quantity triggering alert
    val backupRemindersEnabled: Boolean = true,
    val backupReminderFrequency: String = "WEEKLY", // "DAILY", "WEEKLY", "MONTHLY"
    val debtCollectionAlertsEnabled: Boolean = true,
    val dailySalesTargetAlertsEnabled: Boolean = true,
    val dailyTargetAmount: Double = 3000.0
)
