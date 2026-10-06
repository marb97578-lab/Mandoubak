package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clients",
    indices = [
        Index(value = ["phone"]),
        Index(value = ["name"])
    ]
)
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val location: String = "", // GPS / coordinates or neighborhood
    val currentBalance: Double = 0.0, // debt amount owed by client
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
