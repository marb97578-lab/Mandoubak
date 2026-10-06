package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_receipts",
    indices = [
        Index(value = ["clientId"]),
        Index(value = ["receiptNumber"]),
        Index(value = ["dateMillis"])
    ]
)
data class PaymentReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val receiptNumber: String,
    val clientId: Long,
    val clientName: String,
    val amount: Double,
    val paymentMethod: String = "نقداً", // نقداً / تحويل بنكي / شيك
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)
