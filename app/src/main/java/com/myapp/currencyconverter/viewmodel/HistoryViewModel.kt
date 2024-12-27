package com.myapp.currencyconverter.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myapp.currencyconverter.model.HistoryItem

class HistoryViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    val historyList = MutableLiveData<MutableList<HistoryItem>>(mutableListOf())

    init {
        // Load data dari SharedPreferences saat ViewModel dibuat
        loadHistoryFromPreferences()
    }

    fun addHistoryItem(item: HistoryItem) {
        val currentList = historyList.value ?: mutableListOf()
        currentList.add(0, item) // Tambahkan item ke posisi teratas
        historyList.value = currentList

        // Simpan ke SharedPreferences
        saveHistoryToPreferences(currentList)
    }

    fun clearAllHistory() {
        historyList.value = mutableListOf()
        saveHistoryToPreferences(mutableListOf())
    }

    private fun loadHistoryFromPreferences() {
        val savedHistory = sharedPreferences.getStringSet("history_list", emptySet()) ?: emptySet()
        val historyItems = savedHistory.map { historyString ->
            val parts = historyString.split(",")
            HistoryItem(
                inputAmount = parts[0],
                fromCurrency = parts[1],
                toCurrency = parts[2],
                outputAmount = parts[3],
                timestamp = parts[4].toLong()
            )
        }
        historyList.value = historyItems.toMutableList()
    }

    private fun saveHistoryToPreferences(history: List<HistoryItem>) {
        val historyStrings = history.map { item ->
            "${item.inputAmount},${item.fromCurrency},${item.toCurrency},${item.outputAmount},${item.timestamp}"
        }.toSet()
        sharedPreferences.edit().putStringSet("history_list", historyStrings).apply()
    }
}
