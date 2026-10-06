package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for persisting global currency configuration in Room Database.
 */
@Entity(tableName = "currency_settings")
data class CurrencySettingEntity(
    @PrimaryKey
    val id: Int = 1,
    val selectedCode: String = "YER",
    val customSymbol: String = "﷼",
    val currencyNameAr: String = "ريال يمني",
    val currencyNameEn: String = "Yemeni Rial",
    val countryNameAr: String = "اليمن",
    val symbolPlacement: String = "AFTER_AMOUNT", // AFTER_AMOUNT or BEFORE_AMOUNT
    val decimalPlaces: Int = 2,
    val useGrouping: Boolean = true,
    val updatedAtMillis: Long = System.currentTimeMillis()
)
