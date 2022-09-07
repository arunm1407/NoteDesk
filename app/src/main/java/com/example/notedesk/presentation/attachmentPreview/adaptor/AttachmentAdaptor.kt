package com.example.notedesk.presentation.attachmentPreview.adaptor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappfragment.R
import com.example.notedesk.presentation.attachmentPreview.listener.AttachmentLisenter
import com.example.notedesk.domain.util.storage.InternalStoragePhoto


class AttachmentAdaptor(
    private val attachmentList: MutableList<InternalStoragePhoto>,
    private val attachmentLisenter: AttachmentLisenter,
    private val isDelete: Boolean,
) :
    RecyclerView.Adapter<AttachmentAdaptor.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindItems(
            internalStoragePhoto: InternalStoragePhoto,
            attachmentLisenter: AttachmentLisenter,
            isDelete: Boolean,

            ) {

            itemView.findViewById<TextView>(R.id.tvfileName).text =  "Img-${internalStoragePhoto.name}"



            if (!isDelete) {
                itemView.findViewById<ImageView>(R.id.delete).visibility = View.GONE
            } else {
                itemView.findViewById<ImageView>(R.id.delete).visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                attachmentLisenter.onAttachmentClicked(internalStoragePhoto)
            }

            itemView.findViewById<ImageView>(R.id.delete).setOnClickListener {
                attachmentLisenter.onDelete(internalStoragePhoto, adapterPosition)
                removeItem(position = adapterPosition)
            }


        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.attachment, parent, false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(
            attachmentList[position],
            attachmentLisenter,
            isDelete)
    }

    override fun getItemCount(): Int {
        return attachmentList.size
    }

    private fun removeItem(position: Int) {
        attachmentList.removeAt(position)
        notifyItemRemoved(position)
    }


}

