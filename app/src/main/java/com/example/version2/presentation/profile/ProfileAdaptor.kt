package com.example.version2.presentation.profile


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.version2.R

class ProfileAdaptor(
    private val list: List<ProfileDetails>
) : RecyclerView.Adapter<ProfileAdaptor.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(s: ProfileDetails) {


            itemView.findViewById<ImageView>(R.id.ivImage).apply {
                setImageResource(s.image)
            }



            if (s.title.trim().isNotEmpty()) {
                itemView.findViewById<TextView>(R.id.Title).text = s.title

            } else {
                itemView.findViewById<TextView>(R.id.Title).text = " "

            }
            if (s.content.trim().isNotEmpty()) {
                itemView.findViewById<TextView>(R.id.content).text = s.content

            } else {
                itemView.findViewById<TextView>(R.id.content).text = " - "
            }

        }


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}