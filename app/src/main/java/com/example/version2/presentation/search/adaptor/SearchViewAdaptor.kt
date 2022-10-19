package com.example.version2.presentation.search.adaptor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.version2.R
import com.example.version2.databinding.NoteItemBinding
import com.example.version2.databinding.SuggestionItemBinding
import com.example.version2.databinding.TitleItemBinding
import com.example.version2.presentation.common.viewHolder.NotesRVViewHolder
import com.example.version2.presentation.model.NotesRvItem
import com.example.version2.presentation.search.listener.SuggestionLisenter


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
            R.layout.note_item -> NotesRVViewHolder.NotesViewHolder(
                NoteItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.suggestion_item -> NotesRVViewHolder.SuggestionViewHolder(
                SuggestionItemBinding.inflate(
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
                holder.bind(items[position] as NotesRvItem.UINotes)
                holder.itemView.setOnClickListener {
                    val note = (items[position] as NotesRvItem.UINotes)
                    suggestionListener.addSuggestion(note.note.title)
                    suggestionListener.onClickedNote(note)


                }
            }
            is NotesRVViewHolder.SuggestionViewHolder -> {
                holder.bind(items[position] as NotesRvItem.UISuggestion)
                holder.itemView.setOnClickListener {
                    val suggestion = items[position] as NotesRvItem.UISuggestion
                    suggestionListener.onSuggestionClicked(suggestion.suggestion)
                }

                holder.itemView.findViewById<ImageView>(R.id.ibRemove).setOnClickListener {

                    val suggestion = items[position] as NotesRvItem.UISuggestion
                    suggestionListener.deleteSearchHistory(
                        suggestion.suggestion,
                        holder.adapterPosition
                    )

                }

            }

            is NotesRVViewHolder.TitleViewHolder -> holder.bind(items[position] as NotesRvItem.UITitle)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotesRvItem.UINotes -> R.layout.note_item
            is NotesRvItem.UISuggestion -> R.layout.suggestion_item
            is NotesRvItem.UITitle -> R.layout.title_item
        }
    }


    fun removeItemFromList(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}