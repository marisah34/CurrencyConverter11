package com.myapp.currencyconverter.model

data class HistoryItem(
    val inputAmount: String,
    val fromCurrency: String,
    val toCurrency: String,
    val outputAmount: String,
    val timestamp: Long
)
