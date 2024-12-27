package com.myapp.currencyconverter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapp.currencyconverter.R
import com.myapp.currencyconverter.adapter.HistoryAdapter
import com.myapp.currencyconverter.viewmodel.HistoryViewModel
import com.myapp.currencyconverter.viewmodel.HistoryViewModelFactory

class HistoryFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Inisialisasi ViewModel
        historyViewModel = ViewModelProvider(
            requireActivity(),
            HistoryViewModelFactory(requireContext())
        )[HistoryViewModel::class.java]

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_history)
        val clearAllButton: Button = view.findViewById(R.id.button_clear_all)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(historyViewModel.historyList.value ?: mutableListOf()) { index ->
            val currentList = historyViewModel.historyList.value ?: mutableListOf()
            currentList.removeAt(index)
            historyViewModel.historyList.value = currentList
        }
        recyclerView.adapter = historyAdapter

        historyViewModel.historyList.observe(viewLifecycleOwner) { newList ->
            historyAdapter.updateData(newList)
        }

        clearAllButton.setOnClickListener {
            historyViewModel.clearAllHistory()
        }

        return view
    }
}
