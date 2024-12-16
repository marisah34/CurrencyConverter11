package com.example.currencyconverter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.adapter.NoteAdapter

class NoteFragment : Fragment() {

    private lateinit var editTextNote: EditText
    private lateinit var buttonAddNote: Button
    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private val notes = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        editTextNote = view.findViewById(R.id.editTextNote)
        buttonAddNote = view.findViewById(R.id.buttonAddNote)
        recyclerViewNotes = view.findViewById(R.id.recyclerViewNotes)

        recyclerViewNotes.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter(notes,
            onDelete = { position -> deleteNoteAt(position) },
            onEdit = { position -> editNoteAt(position) }
        )
        recyclerViewNotes.adapter = noteAdapter

        loadNotes()

        buttonAddNote.setOnClickListener {
            addNote()
        }

        return view
    }

    private fun addNote() {
        val newNote = editTextNote.text.toString()
        if (newNote.isNotEmpty()) {
            notes.add(newNote)
            saveNotes()
            noteAdapter.updateData(notes)
            editTextNote.text.clear()
        }
    }

    private fun deleteNoteAt(position: Int) {
        notes.removeAt(position)
        saveNotes()
        noteAdapter.updateData(notes)
    }

    private fun editNoteAt(position: Int) {
        editTextNote.setText(notes[position])
        deleteNoteAt(position)
    }

    private fun saveNotes() {
        val sharedPreferences = requireContext().getSharedPreferences("note_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putStringSet("notes", notes.toSet())
            apply()
        }
    }

    private fun loadNotes() {
        val sharedPreferences = requireContext().getSharedPreferences("note_prefs", Context.MODE_PRIVATE)
        val savedNotes = sharedPreferences.getStringSet("notes", emptySet())
        notes.clear()
        notes.addAll(savedNotes ?: emptySet())
        noteAdapter.updateData(notes)
    }
}
