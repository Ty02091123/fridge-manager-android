package com.fridge.dashboard.data

data class FoodItem(
    val id: Int,
    val name: String,
    val type: String,
    val count: Int,
    val expireDate: String
)
