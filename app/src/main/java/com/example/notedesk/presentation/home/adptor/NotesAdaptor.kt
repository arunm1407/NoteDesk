package com.example.notedesk.presentation.home.adptor

import android.view.*
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappfragment.R
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.activity.MyDiffUtil
import com.example.notedesk.presentation.home.Listener.NotesListener
import com.example.notedesk.presentation.activity.NotesRVViewHolder
import com.example.notesappfragment.databinding.ItemContainerBinding


class NotesAdaptor(
    private val context: FragmentActivity,
    var notes: List<Notes>,
    private val notesListener: NotesListener
) :
    RecyclerView.Adapter<NotesRVViewHolder>() {



    fun setData(note: List<Notes>) {
        val diffUtil = MyDiffUtil(notes, note)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        notes = note
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

            val pos: Int = position
            val data: Notes = notes[pos]
            holder.bind(notes[position])
            holder.itemView.setOnClickListener {
                notesListener.onClick(holder, data)

            }
            holder.itemView.setOnLongClickListener {
                notesListener.onLongClicked(holder, data)
                true
            }
        }

        if (notesListener.isAllSelected()) {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
            holder.itemView.foreground = getDrawable(context, R.drawable.foreground_selected_note)

        } else {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.GONE
            holder.itemView.foreground = null

        }
    }


    override fun getItemCount(): Int {
        return notes.size
    }

}

