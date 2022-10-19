package com.example.version2.presentation.common.viewHolder

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.version2.presentation.util.date.DateUtil
import com.example.version2.R
import com.example.version2.databinding.NoteItemBinding
import com.example.version2.databinding.SuggestionItemBinding
import com.example.version2.databinding.TitleItemBinding
import com.example.version2.domain.model.Priority
import com.example.version2.presentation.model.NotesRvItem
import com.example.version2.presentation.util.checkEmpty
import com.example.version2.presentation.util.setColor
import com.example.version2.presentation.util.toColor


sealed class NotesRVViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    class TitleViewHolder(private val binding: TitleItemBinding) : NotesRVViewHolder(binding) {
        fun bind(title: NotesRvItem.UITitle) {
            binding.textViewTitle.text = title.title
        }
    }

    class SuggestionViewHolder(private val binding: SuggestionItemBinding) :
        NotesRVViewHolder(binding) {
        fun bind(suggestion: NotesRvItem.UISuggestion) {
            binding.tvSuggestion.text = suggestion.suggestion

        }
    }

    class NotesViewHolder(private val binding: NoteItemBinding) : NotesRVViewHolder(binding) {

        @SuppressLint("SetTextI18n")
        fun bind(note: NotesRvItem.UINotes) {
            val notes = note.note
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
            if (notes.attachments.isEmpty()) {
                binding.attachmentCount.text = notes.attachments.size.toString()
                binding.attachmentCount.visibility = View.GONE
            } else {
                binding.attachmentCount.text = "${(notes.attachments.size)}"
                binding.attachmentCount.visibility = View.VISIBLE
            }

            when (notes.priority) {
                Priority.LOW -> binding.rvPriority.setColor(R.color.GREEN)
                Priority.IMPORTANT -> binding.rvPriority.setColor(R.color.RED)
                Priority.MEDIUM -> binding.rvPriority.setColor(R.color.YELLOW)
            }
            if (notes.attachments.size>0)
            {
                binding.attachmentCount.text="(${notes.attachments.size})"
                binding.attachmentCount.visibility = View.VISIBLE
            }
            else{

                binding.attachmentCount.visibility = View.GONE
            }
            binding.checkbox.visibility = View.GONE
            binding.creationDate.text = DateUtil.getDateAndTime(notes.createdTime)
            binding.rvFavoriteImage.isInvisible = !notes.favorite
            binding.layout.setBackgroundColor(notes.color.color.toColor())



        }


    }
}

