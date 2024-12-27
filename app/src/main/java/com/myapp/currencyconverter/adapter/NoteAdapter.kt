package com.myapp.currencyconverter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapp.currencyconverter.R

class NoteAdapter(
    private var notes: MutableList<String>,
    private val onDelete: (Int) -> Unit,
    private val onEdit: (Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteText: TextView = itemView.findViewById(R.id.textNote)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)
        val editButton: ImageButton = itemView.findViewById(R.id.buttonEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.noteText.text = note

        // Handle delete action
        holder.deleteButton.setOnClickListener {
            onDelete(position)
        }

        // Handle edit action
        holder.editButton.setOnClickListener {
            onEdit(position)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun updateData(newNotes: MutableList<String>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
