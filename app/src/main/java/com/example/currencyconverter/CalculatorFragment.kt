package com.example.currencyconverter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.currencyconverter.R

class CalculatorFragment : Fragment() {

    private lateinit var display: TextView
    private var currentNumber: String = ""
    private var operator: String? = null
    private var firstOperand: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        display = view.findViewById(R.id.display)

        // Daftarkan listener untuk semua tombol
        val buttonIds = listOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_clear, R.id.button_plus,
            R.id.button_minus, R.id.button_multiply, R.id.button_divide,
            R.id.button_equals, R.id.button_decimal
        )

        buttonIds.forEach { id ->
            view.findViewById<Button>(id).setOnClickListener { handleButtonClick(id) }
        }

        return view
    }

    private fun handleButtonClick(id: Int) {
        when (id) {
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9 -> {
                // Tambahkan angka ke input saat ini
                val digit = view?.findViewById<Button>(id)?.text.toString()
                currentNumber += digit
                display.text = currentNumber
            }
            R.id.button_clear -> {
                // Reset kalkulator
                currentNumber = ""
                operator = null
                firstOperand = null
                display.text = "0"
            }
            R.id.button_plus, R.id.button_minus, R.id.button_multiply, R.id.button_divide -> {
                // Set operator
                operator = view?.findViewById<Button>(id)?.text.toString()
                firstOperand = currentNumber.toDoubleOrNull()
                currentNumber = ""
            }
            R.id.button_equals -> {
                // Hitung hasil
                val secondOperand = currentNumber.toDoubleOrNull()
                if (firstOperand != null && secondOperand != null && operator != null) {
                    val result = calculateResult(firstOperand!!, secondOperand, operator!!)
                    display.text = result.toString()
                    currentNumber = result.toString()
                    operator = null
                    firstOperand = null
                }
            }
            R.id.button_decimal -> {
                // Tambahkan desimal
                if (!currentNumber.contains(".")) {
                    currentNumber += "."
                    display.text = currentNumber
                }
            }
        }
    }

    private fun calculateResult(first: Double, second: Double, operator: String): Double {
        return when (operator) {
            "+" -> first + second
            "-" -> first - second
            "x" -> first * second
            "/" -> first / second
            else -> 0.0
        }
    }
}
