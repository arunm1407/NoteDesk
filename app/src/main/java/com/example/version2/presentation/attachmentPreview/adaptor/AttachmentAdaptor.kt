package com.example.version2.presentation.attachmentPreview.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.version2.R
import com.example.version2.domain.model.Attachment
import com.example.version2.presentation.attachmentPreview.listener.AttachmentLisenter


class AttachmentAdaptor(
    list: List<Attachment>,
    private val attachmentLisenter: AttachmentLisenter,
    private val isDelete: Boolean,
) :
    RecyclerView.Adapter<AttachmentAdaptor.ViewHolder>() {


    private val attachmentList:MutableList<Attachment> =list.toMutableList()



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(
            name: Attachment,
            attachmentLisenter: AttachmentLisenter,
            isDelete: Boolean,

            ) {

            itemView.setOnClickListener {
                attachmentLisenter.onAttachmentClicked(name.name)
            }
            itemView.findViewById<TextView>(R.id.tvFileName).text = itemView.resources.getString(R.string.attachmentName,name.name)



            if (!isDelete) {
                itemView.findViewById<ImageView>(R.id.delete).visibility = View.GONE
            } else {
                itemView.findViewById<ImageView>(R.id.delete).visibility = View.VISIBLE
            }

            itemView.findViewById<ImageView>(R.id.delete).setOnClickListener {
                attachmentLisenter.onDelete(name.name, adapterPosition)
                removeItem(name,adapterPosition)
            }


        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.attachment_preview, parent, false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(
            attachmentList[position],
            attachmentLisenter,
            isDelete
        )
    }

    override fun getItemCount(): Int {
        return attachmentList.size
    }

    private fun removeItem(name: Attachment, adapterPosition: Int) {
        attachmentList.remove(name)
        notifyItemRemoved(adapterPosition)
    }


}

