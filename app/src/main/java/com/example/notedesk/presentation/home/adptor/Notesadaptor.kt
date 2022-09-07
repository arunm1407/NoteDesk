package com.example.notedesk.presentation.home.adptor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappfragment.R
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notedesk.domain.util.date.DateUtil
import com.example.notedesk.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class Notesadaptor(
    val context: FragmentActivity,
    var notes: List<Notes>,
    val notesSelectedLisenter: (Notes) -> Unit,
    private val requireActivity: FragmentActivity,
    val viewModel: HomeViewModel,
    val viewLifecycleOwner: LifecycleOwner,

    ) :
    RecyclerView.Adapter<ViewHolder>() {
    var isEnable = false
    var isSelectAll = false
    var selectList: ArrayList<Notes> = ArrayList()


    @SuppressLint("NotifyDataSetChanged")
    fun filtering(newFilteredList: ArrayList<Notes>) {
        notes = newFilteredList
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_container, parent, false)

        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos: Int = position
        val data: Notes = notes[pos]
        holder.setNote(data)
        holder.itemView.setOnClickListener {
            if (isEnable) {
                clickItem(holder, data)
            } else {
                notesSelectedLisenter(data)
            }

        }
        holder.itemView.setOnLongClickListener {
            viewModel.setTrueContextualActionMode()

            if (!isEnable) {


                val callback = object : ActionMode.Callback {


                    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        val menuInflater: MenuInflater = mode!!.menuInflater
                        menuInflater.inflate(R.menu.muliptle_delete, menu)
                        return true
                    }

                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        isEnable = true
                        clickItem(holder, data)

                        viewModel.getText().observe(viewLifecycleOwner)
                        {
                            mode?.title = "$it Selected"
                        }

                        return true
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                        when (item!!.itemId) {
                            R.id.menu_md_delete -> {

                                if (selectList.isEmpty()) {
                                    Toast.makeText(
                                        requireActivity,
                                        "No Selection is made",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val builder: AlertDialog.Builder =
                                        AlertDialog.Builder(requireActivity)
                                    builder.setTitle("Are you sure?")
                                        .setMessage("Do you want to Delete the Notes ?")
                                        .setPositiveButton(
                                            "Yes"

                                        ) { _, _ ->

                                            selectList.forEach { note ->
                                                viewModel.deleteNote(note.id)
                                                runBlocking(Dispatchers.IO) {
                                                    viewModel.deleteFile(
                                                        note.id
                                                    )
                                                }

                                            }

                                            mode!!.finish()

                                        }
                                        .setNegativeButton("No")
                                        { _, _ ->
                                            mode!!.finish()
                                        }
                                    val alert: AlertDialog = builder.create()
                                    alert.show()
                                }


                            }
                            R.id.menu_md_selectAll -> {
                                if (selectList.size == notes.size) {
                                    isSelectAll = false
                                    selectList.clear()
                                    viewModel.setText("${selectList.size}")

                                } else {
                                    isSelectAll = true
                                    selectList.clear()
                                    selectList.addAll(notes)
                                    viewModel.setText("${selectList.size}")
                                }
                                notifyDataSetChanged()
                            }
                        }
                        return true

                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDestroyActionMode(mode: ActionMode?) {
                        isEnable = false
                        isSelectAll = false
                        holder.itemView.foreground = null

                        selectList.clear()
                        notifyDataSetChanged()
                        viewModel.contextual.value = false
                    }

                }
                context.startActionMode(callback)

            } else {
                clickItem(holder, data)
            }
            return@setOnLongClickListener true
        }

        if (isSelectAll) {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
            holder.itemView.foreground = getDrawable(context, R.drawable.foreground_selected_note)

        } else {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.GONE
            holder.itemView.foreground = null

        }
    }

    private fun clickItem(holder: ViewHolder, note: Notes) {
        if (holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility == View.GONE) {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
            holder.itemView.foreground = getDrawable(
                requireActivity.applicationContext,
                R.drawable.foreground_selected_note
            )
            selectList.add(note)

        } else {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.GONE
            holder.itemView.foreground = null
            selectList.remove(note)

        }
        viewModel.setText("${selectList.size}")

    }

    override fun getItemCount(): Int {
        return notes.size
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val texttitle = itemView.findViewById<TextView>(R.id.texttitle)
    private val textsubtitle = itemView.findViewById<TextView>(R.id.textsubtitle)
    private val textDescription = itemView.findViewById<TextView>(R.id.textDescription)
    private val linearlayout = itemView.findViewById<ConstraintLayout>(R.id.layoutNote)
    private val createdTime = itemView.findViewById<TextView>(R.id.creationDate)
    private val favorite = itemView.findViewById<ImageView>(R.id.rv_favoriteImage)
    private val priority = itemView.findViewById<ImageView>(R.id.rv_priority)
    private val attachmentCount = itemView.findViewById<TextView>(R.id.attachmentCount)
    fun setNote(note: Notes) {
        if (note.title.trim().isEmpty()) {
            texttitle.visibility = View.GONE
            texttitle.text = note.title


        } else {
            texttitle.text = note.title
            texttitle.visibility = View.VISIBLE
        }
        if (note.subtitle.trim().isEmpty()) {
            textsubtitle.text = note.subtitle
            textsubtitle.visibility = View.GONE

        } else {
            textsubtitle.text = note.subtitle
            textsubtitle.visibility = View.VISIBLE

        }
        if (note.noteText.trim().isEmpty()) {
            textDescription.text = note.noteText
            textDescription.visibility = View.GONE

        } else {
            textDescription.text = note.noteText
            textDescription.visibility = View.VISIBLE
        }
        if (note.attachmentCount == 0) {
            attachmentCount.text = note.attachmentCount.toString()
            attachmentCount.visibility = View.GONE
        } else {
            attachmentCount.text = "${(note.attachmentCount)}"
            attachmentCount.visibility = View.GONE
        }

        when (note.priority) {
            IndentKeys.GREEN -> priority.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.GREEN
                )
            )
            IndentKeys.RED -> priority.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.RED
                )
            )
            IndentKeys.BLUE -> priority.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.BLUE
                )
            )
            IndentKeys.YELLOW -> priority.setColorFilter(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.YELLOW
                )
            )
        }
        createdTime.text = DateUtil.getDateAndTime(note.createdTime)
        val gradientDrawable = linearlayout.background as GradientDrawable
        if (!note.favorite) {
            favorite.visibility = View.GONE
        } else {
            favorite.visibility = View.VISIBLE
        }
        if (note.color.isNotEmpty())
            gradientDrawable.setColor(Color.parseColor(note.color))
        else
            gradientDrawable.setColor(Color.parseColor("#F1F1F1"))


    }


}
