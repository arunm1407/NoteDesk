package com.example.version2.presentation.homeScreen.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.version2.R
import com.example.version2.databinding.NoteItemBinding
import com.example.version2.presentation.common.MyDiffUtil
import com.example.version2.presentation.common.viewHolder.NotesRVViewHolder
import com.example.version2.presentation.homeScreen.listener.NotesListener
import com.example.version2.presentation.model.NotesRvItem


class NotesAdaptor(
    private val context: FragmentActivity,
    notes: List<NotesRvItem.UINotes>,
    private val notesListener: NotesListener
) :
    RecyclerView.Adapter<NotesRVViewHolder>() {


    private var isAllSelected: Boolean = false
    private var _notes: MutableList<NotesRvItem.UINotes> = notes.toMutableList()
    val note: List<NotesRvItem.UINotes>
        get() = _notes


    fun setIsAllSelected(value: Boolean) {
        isAllSelected = value

    }


    fun setData(note: List<NotesRvItem.UINotes>) {
        val diffUtil = MyDiffUtil(_notes, note.toMutableList())
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        _notes = note.toMutableList()
        diffResults.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesRVViewHolder {
        return NotesRVViewHolder.NotesViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    }


    override fun onBindViewHolder(holder: NotesRVViewHolder, position: Int) {

        if (holder is NotesRVViewHolder.NotesViewHolder) {

            holder.bind(_notes[holder.adapterPosition])
            holder.itemView.setOnClickListener {
                notesListener.onClick(holder.adapterPosition)

            }
            holder.itemView.setOnLongClickListener {
                notesListener.onLongClicked(holder.adapterPosition)
                true
            }
        }

        if (isAllSelected) {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
            holder.itemView.foreground =
                AppCompatResources.getDrawable(context, R.drawable.foreground_selected_note)

        } else {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.GONE
            holder.itemView.foreground = null

        }

        notesListener.getSelectedNote().forEach {
            if (it == note[holder.adapterPosition].note) {
                holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
                holder.itemView.foreground =
                    AppCompatResources.getDrawable(context, R.drawable.foreground_selected_note)
            }


        }


    }


    override fun getItemCount(): Int {
        return _notes.size
    }


    fun removeNoteAtPosition(position: Int) {
        _notes.removeAt(position)
    }

    fun getNotesAtPosition(position: Int): NotesRvItem.UINotes {
        return _notes[position]
    }


    fun addNotes(position: Int, notes: NotesRvItem.UINotes) {
        _notes.add(position, notes)
    }


    fun getNoteListSize(): Int {
        return _notes.size
    }


}

