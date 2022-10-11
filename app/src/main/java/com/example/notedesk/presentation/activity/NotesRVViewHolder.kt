package com.example.notedesk.presentation.activity

import android.view.View
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.notedesk.R
import com.example.notedesk.databinding.ItemContainerBinding
import com.example.notedesk.databinding.LayoutSuggestionItemBinding
import com.example.notedesk.databinding.TitleItemBinding
import com.example.notedesk.presentation.model.NotesRvItem
import com.example.notedesk.presentation.util.*
import com.example.notedesk.util.date.DateUtil
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.keys.Keys.COLOR_1


sealed class NotesRVViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    class TitleViewHolder(private val binding: TitleItemBinding) : NotesRVViewHolder(binding) {
        fun bind(title: NotesRvItem.Title) {
            binding.textViewTitle.text = title.title
        }
    }

     class SuggestionViewHolder(private val binding: LayoutSuggestionItemBinding) : NotesRVViewHolder(binding) {
        fun bind(suggestion: NotesRvItem.Suggestion) {
            binding.tvSuggestion.text = suggestion.suggestion
            binding.ivSearch.setImageResource(R.drawable.ic_search)

        }
    }

     class NotesViewHolder(private val binding: ItemContainerBinding) : NotesRVViewHolder(binding) {

        fun bind(note: NotesRvItem.UNotes) {
            val notes=note.toDomainLayer()
            if (notes.title.trim().isEmpty()) {
                binding.textTitle.visibility = View.GONE
                binding.textTitle.text = notes.title


            } else {
                binding.textTitle.text = notes.title
                binding.textTitle.visibility = View.VISIBLE
            }
            if (notes.subtitle.checkEmpty()) {
                binding.textSubtitle.text = notes.subtitle


            } else {
                binding.textSubtitle.text = notes.subtitle
                binding.textSubtitle.visibility = View.VISIBLE

            }
            if (notes.noteText.checkEmpty()) {
                binding.textDescription.text = notes.noteText
                binding.textDescription.visibility = View.GONE

            } else {
                binding.textDescription.text = notes.noteText
                binding.textDescription.visibility = View.VISIBLE
            }
            if (notes.attachmentCount == 0) {
                binding.attachmentCount.text = notes.attachmentCount.toString()
                binding.attachmentCount.visibility = View.GONE
            } else {
                binding.attachmentCount.text = "${(notes.attachmentCount)}"
                binding.attachmentCount.visibility = View.VISIBLE
            }

            when (notes.priority) {
                Keys.GREEN -> binding.rvPriority.setColor(R.color.GREEN)
                Keys.RED -> binding.rvPriority.setColor(R.color.RED)
                Keys.BLUE -> binding.rvPriority.setColor(R.color.BLUE)
                Keys.YELLOW -> binding.rvPriority.setColor( R.color.YELLOW)
            }
            binding.checkbox.visibility = View.GONE
            binding.creationDate.text = DateUtil.getDateAndTime(notes.createdTime)
            binding.rvFavoriteImage.isInvisible = !notes.favorite
            if (notes.attachmentCount>0)
            {
                binding.attachmentCount.text="(${notes.attachmentCount})"
                binding.attachmentCount.visibility = View.VISIBLE
            }
            else{
                binding.attachmentCount.visibility = View.GONE
            }
            if (notes.color.isNotEmpty())
                binding.layout.setBackgroundColor(notes.color.toColor())
            else
                binding.layout.setBackgroundColor(COLOR_1.toColor())



        }


    }
}

