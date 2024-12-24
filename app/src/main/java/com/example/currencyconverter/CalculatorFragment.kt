package com.example.currencyconverter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.currencyconverter.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {

    private lateinit var display: TextView
    private lateinit var viewModel: CalculatorViewModel
    private var activeOperatorButton: Button? = null // Menyimpan tombol operator yang sedang aktif

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        // Inisialisasi ViewModel menggunakan ViewModelProvider
        viewModel = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]

        display = view.findViewById(R.id.display)

        // Tampilkan hasil yang terakhir tersimpan di ViewModel
        display.text = viewModel.result

        // Daftarkan listener untuk semua tombol
        val buttonIds = listOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_clear, R.id.button_plus,
            R.id.button_minus, R.id.button_multiply, R.id.button_divide,
            R.id.button_equals, R.id.button_decimal,R.id.button_sign
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
                val digit = requireView().findViewById<Button>(id).text.toString()
                viewModel.currentNumber += digit
                updateDisplay(viewModel.currentNumber)
            }
            R.id.button_clear -> {
                viewModel.currentNumber = ""
                viewModel.operator = null
                viewModel.firstOperand = null
                viewModel.result = "0"
                activeOperatorButton?.setBackgroundColor(resources.getColor(android.R.color.holo_orange_light)) // Reset warna tombol operator
                activeOperatorButton = null
                updateDisplay(viewModel.result)
            }

            R.id.button_sign -> {
                if (viewModel.currentNumber.isNotEmpty()) {
                    // Hapus karakter terakhir dari currentNumber
                    viewModel.currentNumber = viewModel.currentNumber.dropLast(1)
                }

                // Jika semua angka telah dihapus, set hasil menjadi "0"
                if (viewModel.currentNumber.isEmpty()) {
                    viewModel.currentNumber = "0"
                }

                // Perbarui tampilan dengan angka yang diperbarui
                updateDisplay(viewModel.currentNumber)
            }


            R.id.button_plus, R.id.button_minus, R.id.button_multiply, R.id.button_divide -> {
                // Reset warna tombol operator sebelumnya
                activeOperatorButton?.setBackgroundColor(resources.getColor(android.R.color.holo_orange_light))

                // Ubah warna tombol operator yang baru
                val operatorButton = requireView().findViewById<Button>(id)
                operatorButton.setBackgroundColor(resources.getColor(android.R.color.darker_gray)) // Warna abu-abu muda
                activeOperatorButton = operatorButton

                viewModel.operator = operatorButton.text.toString()
                viewModel.firstOperand = viewModel.currentNumber.toDoubleOrNull()
                viewModel.currentNumber = ""
            }
            R.id.button_equals -> {
                val secondOperand = viewModel.currentNumber.toDoubleOrNull()
                if (viewModel.firstOperand != null && secondOperand != null && viewModel.operator != null) {
                    val result = calculateResult(viewModel.firstOperand!!, secondOperand, viewModel.operator!!)
                    viewModel.result = result
                    viewModel.currentNumber = result
                    viewModel.operator = null
                    viewModel.firstOperand = null

                    // Reset warna tombol operator
                    activeOperatorButton?.setBackgroundColor(resources.getColor(android.R.color.holo_orange_light))
                    activeOperatorButton = null

                    updateDisplay(viewModel.result)
                }
            }
            R.id.button_decimal -> {
                if (!viewModel.currentNumber.contains(".")) {
                    viewModel.currentNumber += "."
                    updateDisplay(viewModel.currentNumber)
                }
            }
        }
    }

    private fun calculateResult(first: Double, second: Double, operator: String): String {
        val result = when (operator) {
            "+" -> first + second
            "-" -> first - second
            "x" -> first * second
            "/" -> first / second
            else -> 0.0
        }
        // Jika hasil adalah bilangan bulat, hilangkan ".0"
        return if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            result.toString()
        }
    }

    private fun updateDisplay(value: String) {
        display.text = value
    }
}
