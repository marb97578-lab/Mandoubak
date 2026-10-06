package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["barcode"]),
        Index(value = ["category"]),
        Index(value = ["name"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val barcode: String = "",
    val batchNumber: String = "",
    val manufacturingDate: String = "",
    val expirationDate: String = "",
    val costPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val stockQuantity: Int = 0,
    val reservedQuantity: Int = 0,
    val minStockThreshold: Int = 5,
    val unit: String = "حبة",
    val category: String = "عام",
    val supplierName: String = "",
    val notes: String = "",
    val imageUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
