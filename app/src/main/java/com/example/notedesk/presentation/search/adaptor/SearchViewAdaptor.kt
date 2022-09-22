package com.example.notedesk.presentation.search.adaptor

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.activity.NotesRVViewHolder
import com.example.notedesk.data.data_source.NotesRvItem
import com.example.notedesk.presentation.search.listner.SuggestionLisenter
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.ItemContainerBinding
import com.example.notesappfragment.databinding.LayoutSuggestionItemBinding
import com.example.notesappfragment.databinding.TitleItemBinding


class SearchViewAdaptor(
    private var items: MutableList<NotesRvItem>,
    private val suggestionListener: SuggestionLisenter,


    ) :
    RecyclerView.Adapter<NotesRVViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(item: MutableList<NotesRvItem>) {
        items = item
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesRVViewHolder {
        return when (viewType) {
            R.layout.title_item -> NotesRVViewHolder.TitleViewHolder(
                TitleItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_container -> NotesRVViewHolder.NotesViewHolder(
                ItemContainerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.layout_suggestion_item -> NotesRVViewHolder.SuggestionViewHolder(
                LayoutSuggestionItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: NotesRVViewHolder, position: Int) {
        when (holder) {
            is NotesRVViewHolder.NotesViewHolder -> {
                holder.bind(items[position] as Notes)
                holder.itemView.setOnClickListener {
                    Log.i("arun", "inside bind")
                    val searchNote = (items[position] as Notes)
                    val notes = Notes(
                        searchNote.title,
                        searchNote.subtitle,
                        searchNote.createdTime,
                        searchNote.modifiedTime,
                        searchNote.noteText,
                        searchNote.color,
                        searchNote.weblink,
                        searchNote.priority,
                        searchNote.attachmentCount,
                        searchNote.favorite,
                        searchNote.id
                    )

                    suggestionListener.addSuggestion(notes.title)
                    suggestionListener.onClickedNote(notes)


                }
            }
            is NotesRVViewHolder.SuggestionViewHolder -> {
                holder.bind(items[position] as NotesRvItem.Suggestion)
                holder.itemView.setOnClickListener {
                    val suggestion = items[position] as NotesRvItem.Suggestion
                    suggestionListener.onSuggestionClicked(suggestion.suggestion)
                }

                holder.itemView.findViewById<ImageView>(R.id.ibRemove).setOnClickListener {

                    val suggestion = items[position] as NotesRvItem.Suggestion
                    suggestionListener.deleteSearchHistory(suggestion.suggestion)

                }

            }

            is NotesRVViewHolder.TitleViewHolder -> holder.bind(items[position] as NotesRvItem.Title)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Notes -> R.layout.item_container
            is NotesRvItem.Suggestion -> R.layout.layout_suggestion_item
            is NotesRvItem.Title -> R.layout.title_item
        }
    }
}