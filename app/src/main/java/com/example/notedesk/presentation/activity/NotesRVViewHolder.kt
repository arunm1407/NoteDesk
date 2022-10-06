package com.example.notedesk.presentation.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.notedesk.R
import com.example.notedesk.databinding.ItemContainerBinding
import com.example.notedesk.databinding.LayoutSuggestionItemBinding
import com.example.notedesk.databinding.TitleItemBinding
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.Model.NotesRvItem
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
        @SuppressLint("SetTextI18n")
        fun bind(note: Note) {
            if (note.title.trim().isEmpty()) {
                binding.textTitle.visibility = View.GONE
                binding.textTitle.text = note.title


            } else {
                binding.textTitle.text = note.title
                binding.textTitle.visibility = View.VISIBLE
            }
            if (note.subtitle.trim().isEmpty()) {
                binding.textSubtitle.text = note.subtitle
                binding.textSubtitle.visibility = View.GONE

            } else {
                binding.textSubtitle.text = note.subtitle
                binding.textSubtitle.visibility = View.VISIBLE

            }
            if (note.noteText.trim().isEmpty()) {
                binding.textDescription.text = note.noteText
                binding.textDescription.visibility = View.GONE

            } else {
                binding.textDescription.text = note.noteText
                binding.textDescription.visibility = View.VISIBLE
            }
            if (note.attachmentCount == 0) {
                binding.attachmentCount.text = note.attachmentCount.toString()
                binding.attachmentCount.visibility = View.GONE
            } else {
                binding.attachmentCount.text = "${(note.attachmentCount)}"
                binding.attachmentCount.visibility = View.GONE
            }

            when (note.priority) {
                Keys.GREEN -> binding.rvPriority.setColorFilter(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.GREEN
                    )
                )
                Keys.RED -> binding.rvPriority.setColorFilter(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.RED
                    )
                )
                Keys.BLUE -> binding.rvPriority.setColorFilter(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.BLUE
                    )
                )
                Keys.YELLOW -> binding.rvPriority.setColorFilter(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.YELLOW
                    )
                )
            }
            binding.checkbox.visibility = View.GONE
            binding.creationDate.text = DateUtil.getDateAndTime(note.createdTime)

            binding.rvFavoriteImage.isInvisible = !note.favorite
            if (note.attachmentCount>0)
            {
                binding.attachmentCount.text="(${note.attachmentCount})"
                binding.attachmentCount.visibility=View.VISIBLE
            }
            else{
                binding.attachmentCount.visibility=View.GONE
            }
            if (note.color.isNotEmpty())
                binding.layout.setBackgroundColor(Color.parseColor(note.color))
            else
                binding.layout.setBackgroundColor(Color.parseColor(COLOR_1))



        }


    }
}

