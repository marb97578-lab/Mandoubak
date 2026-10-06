package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "suppliers",
    indices = [
        Index(value = ["phone"]),
        Index(value = ["name"])
    ]
)
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val companyName: String = "",
    val outstandingBalance: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
