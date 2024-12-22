package com.example.currencyconverter

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.currencyconverter.model.HistoryItem
import com.example.currencyconverter.viewmodel.HistoryViewModel
import com.example.currencyconverter.viewmodel.HistoryViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var baseCurrency = "EUR"
    private var convertedCurrency = "USD"
    private var currencyRates = mutableMapOf<String, Double>()

    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(applicationContext)
    }

    private lateinit var amountInput: EditText
    private lateinit var resultText: TextView
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        amountInput = findViewById(R.id.amountInput)
        resultText = findViewById(R.id.resultText)
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)

        val convertButton: Button = findViewById(R.id.convertButton)
        val swapButton: Button = findViewById(R.id.swapButton)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.itemIconTintList = null

        fetchExchangeRates()

        convertButton.setOnClickListener {
            if (amountInput.text.isNotEmpty()) {
                convertCurrency()
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }

        swapButton.setOnClickListener {
            swapCurrencies()
        }

        if (savedInstanceState == null) {
            showConversionElements()
            bottomNavigationView.selectedItemId = R.id.navigation_convert
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_note -> {
                    hideConversionElements()
                    loadFragment(NoteFragment())
                }

                R.id.navigation_calculator -> {
                    hideConversionElements()
                    loadFragment(CalculatorFragment())
                }

                R.id.navigation_history -> {
                    hideConversionElements()
                    loadFragment(HistoryFragment())
                }

                R.id.navigation_chart -> {
                    hideConversionElements()
                    loadFragment(ChartFragment())
                }

                R.id.navigation_convert -> {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                    supportFragmentManager.fragments.forEach { fragment ->
                        supportFragmentManager.beginTransaction().remove(fragment).commitNow()
                    }

                    showConversionElements()
                }
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContent, fragment)
            .commit()
    }

    private fun hideConversionElements() {
        amountInput.visibility = View.GONE
        resultText.visibility = View.GONE
        findViewById<Button>(R.id.convertButton).visibility = View.GONE
        findViewById<Button>(R.id.swapButton).visibility = View.GONE
        fromCurrencySpinner.visibility = View.GONE
        toCurrencySpinner.visibility = View.GONE
    }

    private fun showConversionElements() {
        amountInput.visibility = View.VISIBLE
        resultText.visibility = View.VISIBLE
        findViewById<Button>(R.id.convertButton).visibility = View.VISIBLE
        findViewById<Button>(R.id.swapButton).visibility = View.VISIBLE
        fromCurrencySpinner.visibility = View.VISIBLE
        toCurrencySpinner.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun convertCurrency() {
        if (baseCurrency == convertedCurrency) {
            resultText.text = "${amountInput.text} $baseCurrency"
            return
        }

        try {
            val input = amountInput.text.toString().toDouble()
            val baseRate = currencyRates[baseCurrency] ?: return
            val targetRate = currencyRates[convertedCurrency] ?: return

            val result = (targetRate / baseRate) * input
            resultText.text = "%.2f $convertedCurrency".format(result)

            val historyItem = HistoryItem(
                inputAmount = amountInput.text.toString(),
                fromCurrency = baseCurrency,
                toCurrency = convertedCurrency,
                outputAmount = "%.2f".format(result),
                timestamp = System.currentTimeMillis()
            )
            historyViewModel.addHistoryItem(historyItem)
        } catch (e: Exception) {
            Log.e("Conversion", "Error converting: ${e.message}")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchExchangeRates() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val API = "https://api.currencyfreaks.com/latest?apikey=a5ddf94800f54361a6d3ad1210b4658c"
                val response = URL(API).readText()
                val jsonObject = JSONObject(response)
                val rates = jsonObject.getJSONObject("rates")

                val desiredCurrencies = resources.getStringArray(R.array.currencies).map { it.split(" - ")[0] }

                currencyRates.clear()
                rates.keys().forEach { currency ->
                    if (currency in desiredCurrencies) {
                        currencyRates[currency] = rates.getDouble(currency)
                    }
                }

                withContext(Dispatchers.Main) {
                    updateCurrencySpinners(desiredCurrencies)
                }
            } catch (e: Exception) {
                Log.e("API", "Error fetching rates: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Error fetching exchange rates: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // In MainActivity.kt, replace the updateCurrencySpinners function with this:

    private fun updateCurrencySpinners(currencies: List<String>) {
        val currencyDescriptions = resources.getStringArray(R.array.currencies)

        // Set up basic adapter for visual representation
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyDescriptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrencySpinner.adapter = adapter
        toCurrencySpinner.adapter = adapter

        // Disable default spinner dropdown
        fromCurrencySpinner.isClickable = false
        toCurrencySpinner.isClickable = false

        // Set up click listeners to show custom searchable spinner
        fromCurrencySpinner.setOnTouchListener { _, _ ->
            CustomSearchableSpinner(this, currencyDescriptions) { position ->
                fromCurrencySpinner.setSelection(position)
                baseCurrency = currencies[position]
            }.show()
            true
        }

        toCurrencySpinner.setOnTouchListener { _, _ ->
            CustomSearchableSpinner(this, currencyDescriptions) { position ->
                toCurrencySpinner.setSelection(position)
                convertedCurrency = currencies[position]
            }.show()
            true
        }
    }

    private fun swapCurrencies() {
        Log.d("Swap", "Before swap: baseCurrency = $baseCurrency, convertedCurrency = $convertedCurrency")

        // Tukar nilai mata uang dasar dan mata uang tujuan
        val temp = baseCurrency
        baseCurrency = convertedCurrency
        convertedCurrency = temp

        // Dapatkan posisi kode mata uang berdasarkan array currencies
        val currencyArray = resources.getStringArray(R.array.currencies)
        val baseIndex = currencyArray.indexOfFirst { it.startsWith(baseCurrency) }
        val convertedIndex = currencyArray.indexOfFirst { it.startsWith(convertedCurrency) }

        // Set spinner hanya jika posisi ditemukan
        if (baseIndex != -1) fromCurrencySpinner.setSelection(baseIndex)
        if (convertedIndex != -1) toCurrencySpinner.setSelection(convertedIndex)

        Log.d("Swap", "After swap: baseCurrency = $baseCurrency, convertedCurrency = $convertedCurrency")
    }

}
