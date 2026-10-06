package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_invoices",
    indices = [
        Index(value = ["clientId"]),
        Index(value = ["invoiceNumber"]),
        Index(value = ["dateMillis"])
    ]
)
data class SaleInvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val clientId: Long,
    val clientName: String,
    val subtotalAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val profitAmount: Double = 0.0,
    val isCredit: Boolean = false,
    val paymentMethod: String = "نقدي",
    val itemsSummary: String = "",
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)
