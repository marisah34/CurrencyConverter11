package com.example.currencyconverter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.adapter.NoteAdapter

class NoteFragment : Fragment() {

    private lateinit var searchViewNotes: SearchView
    private lateinit var editTextNote: EditText
    private lateinit var buttonAddNote: Button
    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private val notes = mutableListOf<String>()
    private val filteredNotes = mutableListOf<String>() // Daftar catatan yang difilter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        // Inisialisasi UI
        searchViewNotes = view.findViewById(R.id.searchViewNotes)
        editTextNote = view.findViewById(R.id.editTextNote)
        buttonAddNote = view.findViewById(R.id.buttonAddNote)
        recyclerViewNotes = view.findViewById(R.id.recyclerViewNotes)

        // Set up RecyclerView
        recyclerViewNotes.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter(filteredNotes,
            onDelete = { position -> deleteNoteAt(position) },
            onEdit = { position -> editNoteAt(position) }
        )
        recyclerViewNotes.adapter = noteAdapter

        // Memuat catatan
        loadNotes()

        // Tombol tambah catatan
        buttonAddNote.setOnClickListener {
            addNote()
        }

        // Fungsi pencarian
        searchViewNotes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })

        return view
    }

    private fun addNote() {
        val newNote = editTextNote.text.toString().trim()
        if (newNote.isNotEmpty()) {
            notes.add(0, newNote) // Menambahkan catatan baru di posisi paling atas
            saveNotes()
            filterNotes(searchViewNotes.query.toString()) // Filter ulang jika ada pencarian aktif
            editTextNote.text.clear()
        }
    }

    private fun deleteNoteAt(position: Int) {
        val actualPosition = notes.indexOf(filteredNotes[position]) // Posisi sebenarnya di daftar utama
        notes.removeAt(actualPosition)
        saveNotes()
        filterNotes(searchViewNotes.query.toString())
    }

    private fun editNoteAt(position: Int) {
        editTextNote.setText(filteredNotes[position])
        deleteNoteAt(position)
    }

    private fun saveNotes() {
        val sharedPreferences = requireContext().getSharedPreferences("note_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putStringSet("notes", notes.toSet()) // Menyimpan catatan
            apply()
        }
    }

    private fun loadNotes() {
        val sharedPreferences = requireContext().getSharedPreferences("note_prefs", Context.MODE_PRIVATE)
        val savedNotes = sharedPreferences.getStringSet("notes", emptySet())
        notes.clear()
        notes.addAll(savedNotes?.sortedDescending() ?: emptyList()) // Memastikan catatan terbaru di atas
        filterNotes(null) // Menampilkan semua catatan saat pertama kali
    }

    private fun filterNotes(query: String?) {
        filteredNotes.clear()
        if (query.isNullOrEmpty()) {
            filteredNotes.addAll(notes)
        } else {
            filteredNotes.addAll(notes.filter { it.contains(query, ignoreCase = true) })
        }
        noteAdapter.updateData(filteredNotes)
    }
}
