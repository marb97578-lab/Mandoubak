package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchases",
    indices = [
        Index(value = ["supplierId"]),
        Index(value = ["invoiceNumber"]),
        Index(value = ["dateMillis"])
    ]
)
data class PurchaseInvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val supplierId: Long = 0,
    val supplierName: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val subtotalAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double = totalAmount - paidAmount,
    val paymentMethod: String = "نقداً",
    val isCredit: Boolean = false,
    val itemsSummary: String = "",
    val notes: String = ""
)
