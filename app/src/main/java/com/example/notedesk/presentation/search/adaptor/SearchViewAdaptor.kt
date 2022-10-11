package com.example.notedesk.presentation.search.adaptor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.notedesk.R
import com.example.notedesk.databinding.ItemContainerBinding
import com.example.notedesk.databinding.LayoutSuggestionItemBinding
import com.example.notedesk.databinding.TitleItemBinding
import com.example.notedesk.presentation.model.NotesRvItem
import com.example.notedesk.presentation.activity.NotesRVViewHolder
import com.example.notedesk.presentation.search.listner.SuggestionLisenter


class SearchViewAdaptor(
    list: List<NotesRvItem>,
    private val suggestionListener: SuggestionLisenter
) :
    RecyclerView.Adapter<NotesRVViewHolder>() {

    private var items: MutableList<NotesRvItem> = list.toMutableList()


    @SuppressLint("NotifyDataSetChanged")
    fun setData(item: List<NotesRvItem>) {
        items = item.toMutableList()
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
                holder.bind(items[position] as NotesRvItem.UNotes)
                holder.itemView.setOnClickListener {
                    val note = (items[position] as NotesRvItem.UNotes)
                    suggestionListener.addSuggestion(note.note.title)
                    suggestionListener.onClickedNote(note)


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
                    suggestionListener.deleteSearchHistory(
                        suggestion.suggestion,
                        holder.adapterPosition
                    )

                }

            }

            is NotesRVViewHolder.TitleViewHolder -> holder.bind(items[position] as NotesRvItem.Title)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotesRvItem.UNotes -> R.layout.item_container
            is NotesRvItem.Suggestion -> R.layout.layout_suggestion_item
            is NotesRvItem.Title -> R.layout.title_item
        }
    }


    fun removeItemFromList(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}