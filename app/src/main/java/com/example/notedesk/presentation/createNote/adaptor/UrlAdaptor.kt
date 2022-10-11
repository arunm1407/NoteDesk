package com.example.notedesk.presentation.createNote.adaptor


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notedesk.R
import com.example.notedesk.presentation.createNote.listener.UrlListener

class UrlAdaptor(
    list: List<String>,
    private val url: UrlListener,
    private val isRemove:Boolean,
) :
    RecyclerView.Adapter<UrlAdaptor.ViewHolder>() {

    private val webList:MutableList<String> =list.toMutableList()




    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(s: String) {


            itemView.findViewById<TextView>(R.id.textWebUrl).text = s
            itemView.findViewById<ImageView>(R.id.removeUrl).visibility=View.INVISIBLE
           if (isRemove)
           {  itemView.findViewById<ImageView>(R.id.removeUrl).visibility = View.VISIBLE
               itemView.findViewById<ImageView>(R.id.removeUrl).setOnClickListener()
               {
                   url.removeUrl(s)
                   notifyItemRemoved(adapterPosition)
               }

           }



        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UrlAdaptor.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.url_container, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: UrlAdaptor.ViewHolder, position: Int) {

        holder.bindItems(webList[position])
    }

    override fun getItemCount(): Int {
        return webList.size
    }
}