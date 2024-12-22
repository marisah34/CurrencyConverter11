package com.example.currencyconverter

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog

class CustomSearchableSpinner(
    private val context: Context,
    private val items: Array<String>,
    private val onItemSelected: (Int) -> Unit
) {
    private lateinit var dialog: Dialog
    private lateinit var listView: ListView
    private lateinit var searchEditText: EditText
    private val filteredItems = mutableListOf<String>()
    private val adapter: ArrayAdapter<String>

    init {
        filteredItems.addAll(items)
        adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, filteredItems)
    }

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.searchable_spinner_dialog, null)

        searchEditText = dialogView.findViewById(R.id.search_edit_text)
        listView = dialogView.findViewById(R.id.list_view)
        listView.adapter = adapter

        dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        setupSearchListener()
        setupItemClickListener()

        dialog.show()
    }

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterItems(s.toString())
            }
        })
    }

    private fun setupItemClickListener() {
        listView.setOnItemClickListener { _, _, position, _ ->
            // Get the selected item from filtered list
            val selectedItem = filteredItems[position]
            // Find its position in the original list
            val originalPosition = items.indexOf(selectedItem)
            // Call the callback with the original position
            onItemSelected(originalPosition)
            dialog.dismiss()
        }
    }

    private fun filterItems(searchText: String) {
        filteredItems.clear()
        if (searchText.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            items.filter {
                it.lowercase().contains(searchText.lowercase())
            }.forEach {
                filteredItems.add(it)
            }
        }
        adapter.notifyDataSetChanged()
    }
}