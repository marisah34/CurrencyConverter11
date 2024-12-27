package com.myapp.currencyconverter.viewmodel

import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    var currentNumber: String = ""
    var operator: String? = null
    var firstOperand: Double? = null
    var result: String = "0"
}
