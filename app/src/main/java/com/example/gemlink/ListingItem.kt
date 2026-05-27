package com.example.gemlink

data class ListingItem(
    val gemName: String,
    val price: String,
    val listedDate: String,
    val imageRes: Int,
    var isSold: Boolean = false
)