package com.example.currencyconverter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.model.HistoryItem

class HistoryAdapter(
    private var historyList: List<HistoryItem>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputText: TextView = itemView.findViewById(R.id.text_input)
        val outputText: TextView = itemView.findViewById(R.id.text_output)
        val deleteButton: Button = itemView.findViewById(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.inputText.text = "${item.inputAmount} ${item.fromCurrency}"
        holder.outputText.text = "${item.outputAmount} ${item.toCurrency}"

        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newHistoryList: List<HistoryItem>) {
        historyList = newHistoryList
        notifyDataSetChanged()
    }
}
