package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["category"]),
        Index(value = ["dateMillis"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "عام", // وقود، صيانة، إيجار، ضيافة، رواتب، عام
    val amount: Double,
    val dateMillis: Long = System.currentTimeMillis(),
    val paymentMethod: String = "نقداً",
    val notes: String = ""
)
