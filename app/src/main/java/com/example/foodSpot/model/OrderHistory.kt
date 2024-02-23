package com.example.foodSpot.model

data class OrderHistory(
    val orderId: String,
    val restaurantName: String,
    val totalCost: String,
    val date: String,
    val userId : String?
)