
package com.myapp.currencyconverter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.myapp.currencyconverter.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.random.Random

class ChartFragment : Fragment() {

    private lateinit var currencyTextView: TextView
    private lateinit var lineCharts: Map<String, LineChart>
    private lateinit var searchView: SearchView
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval: Long = 30_000 // Interval pembaruan (30 detik)
    private val chartData: MutableMap<String, MutableList<Entry>> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = "Chart"

        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        currencyTextView = view.findViewById(R.id.currency_data)
        searchView = view.findViewById(R.id.search_view)

        lineCharts = mapOf(
            "EUR" to view.findViewById(R.id.line_chart_eur),
            "USD" to view.findViewById(R.id.line_chart_usd),
            "VND" to view.findViewById(R.id.line_chart_vnd),
            "AUD" to view.findViewById(R.id.line_chart_aud),
            "JPY" to view.findViewById(R.id.line_chart_jpy),
            "CAD" to view.findViewById(R.id.line_chart_cad),
            "DKK" to view.findViewById(R.id.line_chart_dkk),
            "SEK" to view.findViewById(R.id.line_chart_sek),
            "IDR" to view.findViewById(R.id.line_chart_idr),
            "GBP" to view.findViewById(R.id.line_chart_gbp),
            "CNY" to view.findViewById(R.id.line_chart_cny),
            "HKD" to view.findViewById(R.id.line_chart_hkd),
            "KRW" to view.findViewById(R.id.line_chart_krw),
            "SGD" to view.findViewById(R.id.line_chart_sgd),
            "MYR" to view.findViewById(R.id.line_chart_myr),
            "THB" to view.findViewById(R.id.line_chart_thb),
            "NZD" to view.findViewById(R.id.line_chart_nzd),
            "CHF" to view.findViewById(R.id.line_chart_chf)
        )

        // Inisialisasi data grafik
        lineCharts.keys.forEach { chartData[it] = mutableListOf() }

        fetchHistoricalData() // Panggilan pertama untuk data
        scheduleRealTimeUpdates() // Memulai pembaruan otomatis

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCharts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCharts(newText)
                return true
            }
        })

        return view
    }

    private fun filterCharts(query: String?) {
        val filterQuery = query?.toUpperCase() ?: ""
        lineCharts.keys.forEach { currency ->
            val lineChart = lineCharts[currency]
            lineChart?.visibility = if (currency.contains(filterQuery)) View.VISIBLE else View.GONE
        }
    }

    private fun fetchHistoricalData() {
        val API_URL = "https://api.currencyfreaks.com/latest?apikey=36ffd78597494721911b6f71cf925b3b"
        val desiredCurrencies = listOf("EUR", "USD", "VND", "AUD", "JPY", "IDR", "CAD", "DKK", "SEK", "GBP", "CNY", "HKD", "KRW", "SGD", "MYR", "THB", "NZD", "CHF")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(API_URL).readText()
                val jsonObject = JSONObject(response)
                val rates = jsonObject.getJSONObject("rates")

                val currencyData = StringBuilder()

                withContext(Dispatchers.Main) {
                    desiredCurrencies.forEach { currency ->
                        var rate = rates.getString(currency).toFloat()

                        // Simulasikan fluktuasi acak (misalnya ± 0.02%)
                        val fluctuation = (Random.nextFloat() * 0.04f - 0.02f)
                        rate += rate * fluctuation

                        currencyData.append("$currency: $rate\n")

                        val existingEntries = chartData[currency] ?: mutableListOf()
                        val nextIndex = if (existingEntries.isEmpty()) 0f else existingEntries.last().x + 1
                        existingEntries.add(Entry(nextIndex, rate))
                        chartData[currency] = existingEntries

                        lineCharts[currency]?.let { lineChart ->
                            updateChart(lineChart, existingEntries, "$currency Rates")
                        }
                    }

                    currencyTextView.text = currencyData.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    currencyTextView.text = "Failed to fetch data"
                }
            }
        }
    }

    private fun updateChart(lineChart: LineChart, entries: MutableList<Entry>, label: String) {
        val dataSet = LineDataSet(entries, label).apply {
            color = android.graphics.Color.BLUE
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
        }

        val trendDataSet = LineDataSet(entries.mapIndexed { index, entry ->
            Entry(entry.x, entries.take(index + 1).averageBy { it.y })
        }, "Trend Line").apply {
            color = android.graphics.Color.RED
            lineWidth = 1.5f
            circleRadius = 0f
            setDrawCircles(false)
        }

        val lineData = LineData(dataSet, trendDataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun scheduleRealTimeUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchHistoricalData()
                handler.postDelayed(this, refreshInterval)
            }
        }, refreshInterval)
    }

    private fun List<Entry>.averageBy(selector: (Entry) -> Float): Float {
        return this.map { selector(it) }.average().toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}