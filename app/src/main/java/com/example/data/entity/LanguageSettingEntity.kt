package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for persisting the selected application language securely in Room Database.
 */
@Entity(tableName = "language_settings")
data class LanguageSettingEntity(
    @PrimaryKey
    val id: Int = 1,
    val selectedCode: String = "ar",
    val nameNative: String = "العربية",
    val nameAr: String = "العربية",
    val nameEn: String = "Arabic",
    val isRtl: Boolean = true,
    val flagEmoji: String = "🇸🇦",
    val updatedAtMillis: Long = System.currentTimeMillis()
)
