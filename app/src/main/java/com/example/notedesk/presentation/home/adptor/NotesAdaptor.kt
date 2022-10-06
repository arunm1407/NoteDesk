package com.example.notedesk.presentation.home.adptor

import android.view.*
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notedesk.R
import com.example.notedesk.databinding.ItemContainerBinding
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.activity.MyDiffUtil
import com.example.notedesk.presentation.home.listener.NotesListener
import com.example.notedesk.presentation.activity.NotesRVViewHolder


class NotesAdaptor(
    private val context: FragmentActivity,
    notes: List<Note>,
    private val notesListener: NotesListener
) :
    RecyclerView.Adapter<NotesRVViewHolder>() {


    private var isAllSelected: Boolean = false
    private var _notes: MutableList<Note> = notes.toMutableList()
    val note: List<Note>
        get() = _notes


    fun setIsAllSelected(value: Boolean) {
        isAllSelected = value

    }


    fun setData(note: List<Note>) {
        val diffUtil = MyDiffUtil(_notes, note.toMutableList())
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        _notes = note.toMutableList()
        diffResults.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesRVViewHolder {
        return NotesRVViewHolder.NotesViewHolder(
            ItemContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    }


    override fun onBindViewHolder(holder: NotesRVViewHolder, position: Int) {

        if (holder is NotesRVViewHolder.NotesViewHolder) {

            holder.bind(_notes[position])
            holder.itemView.setOnClickListener {
                notesListener.onClick(position)

            }
            holder.itemView.setOnLongClickListener {
                notesListener.onLongClicked(position)
                true
            }
        }

        if (isAllSelected) {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
            holder.itemView.foreground = getDrawable(context, R.drawable.foreground_selected_note)

        } else {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.GONE
            holder.itemView.foreground = null

        }

      notesListener.getSelectedNote().forEach {
            if (it == note[holder.adapterPosition]) {
                holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
                holder.itemView.foreground =
                    getDrawable(context, R.drawable.foreground_selected_note)
            }




        }


    }


    override fun getItemCount(): Int {
        return _notes.size
    }


    fun removeNoteAtPosition(position: Int) {
        _notes.removeAt(position)
    }

    fun getNotesAtPosition(position: Int): Note {
        return _notes[position]
    }


    fun addNotes(position: Int, notes: Note) {
        _notes.add(position, notes)
    }


    fun getNoteListSize(): Int {
        return _notes.size
    }


}

