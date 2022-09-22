package com.example.notedesk.presentation.createNote.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.notedesk.data.data_source.Priority
import com.example.notesappfragment.R

class PriorityAdaptor(context: Context,list: List<Priority>): ArrayAdapter<Priority>(context,0,list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }



    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }
    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {

        val priority=getItem(position)
        val view=convertView?:LayoutInflater.from(context).inflate(R.layout.dropdown_item,parent,false)
        view.findViewById<ImageView>(R.id.priorty_image).setImageResource(priority!!.imageView)
        view.findViewById<TextView>(R.id.prioity_name).text=priority.name

        return view

    }

}