package com.example.currencyconverter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

class ChartFragment : Fragment() {

    private lateinit var currencyTextView: TextView
    private lateinit var lineChartEUR: LineChart
    private lateinit var lineChartUSD: LineChart
    private lateinit var lineChartVND: LineChart
    private lateinit var lineChartAUD: LineChart
    private lateinit var lineChartJPY: LineChart
    private lateinit var lineChartIDR: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        currencyTextView = view.findViewById(R.id.currency_data)
        lineChartEUR = view.findViewById(R.id.line_chart_eur)
        lineChartUSD = view.findViewById(R.id.line_chart_usd)
        lineChartVND = view.findViewById(R.id.line_chart_vnd)
        lineChartAUD = view.findViewById(R.id.line_chart_aud)
        lineChartJPY = view.findViewById(R.id.line_chart_jpy)
        lineChartIDR = view.findViewById(R.id.line_chart_idr)

        fetchHistoricalData() // Fetch historical data for chart
        return view
    }

    private fun fetchHistoricalData() {
        val API_URL = "https://api.currencyfreaks.com/latest?apikey=a5ddf94800f54361a6d3ad1210b4658c"
        val desiredCurrencies = listOf("EUR", "USD", "VND", "AUD", "JPY", "IDR")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(API_URL).readText()
                val jsonObject = JSONObject(response)
                val rates = jsonObject.getJSONObject("rates")

                val currencyData = StringBuilder()

                // Menyusun data untuk setiap chart
                val eurEntries = mutableListOf<Entry>()
                val usdEntries = mutableListOf<Entry>()
                val vndEntries = mutableListOf<Entry>()
                val audEntries = mutableListOf<Entry>()
                val jpyEntries = mutableListOf<Entry>()
                val idrEntries = mutableListOf<Entry>()

                for ((index, currency) in desiredCurrencies.withIndex()) {
                    val rate = rates.getString(currency).toFloat()
                    currencyData.append("$currency: $rate\n")

                    // Menambahkan data untuk setiap grafik
                    when (currency) {
                        "EUR" -> eurEntries.add(Entry(index.toFloat(), rate))
                        "USD" -> usdEntries.add(Entry(index.toFloat(), rate))
                        "VND" -> vndEntries.add(Entry(index.toFloat(), rate))
                        "AUD" -> audEntries.add(Entry(index.toFloat(), rate))
                        "JPY" -> jpyEntries.add(Entry(index.toFloat(), rate))
                        "IDR" -> idrEntries.add(Entry(index.toFloat(), rate))
                    }
                }

                withContext(Dispatchers.Main) {
                    currencyTextView.text = currencyData.toString()

                    // Update chart untuk setiap mata uang
                    updateChart(lineChartEUR, eurEntries, "EUR Rates")
                    updateChart(lineChartUSD, usdEntries, "USD Rates")
                    updateChart(lineChartVND, vndEntries, "VND Rates")
                    updateChart(lineChartAUD, audEntries, "AUD Rates")
                    updateChart(lineChartJPY, jpyEntries, "JPY Rates")
                    updateChart(lineChartIDR, idrEntries, "IDR Rates")
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
        val dataSet = LineDataSet(entries, label)
        dataSet.color = android.graphics.Color.BLUE // Warna garis
        dataSet.valueTextColor = android.graphics.Color.BLACK // Warna teks label titik

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate() // Refresh chart
    }
}
