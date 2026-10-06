package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "supplier_payments",
    indices = [
        Index(value = ["supplierId"]),
        Index(value = ["paymentNumber"]),
        Index(value = ["dateMillis"])
    ]
)
data class SupplierPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val paymentNumber: String,
    val supplierId: Long,
    val supplierName: String,
    val amount: Double,
    val paymentMethod: String = "نقداً",
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)
