package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_movements",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["dateMillis"])
    ]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val movementType: String, // "STOCK_IN", "STOCK_OUT", "RETURNED", "DAMAGED", "ADJUSTMENT"
    val movementTypeArabic: String = "", // "توريد بضاعة", "صرف مبيعات", "مرتجع", "تالف وهالك", "تسوية جرد"
    val quantity: Int,
    val previousStock: Int = 0,
    val newStock: Int = 0,
    val referenceNumber: String = "",
    val reason: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)
