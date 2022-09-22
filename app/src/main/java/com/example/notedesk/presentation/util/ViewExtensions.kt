package com.example.notedesk.presentation.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected


fun View.hideKeyboard() {
    val context = this.context ?: return
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(
        this.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun RecyclerView.initRecyclerView(
    layoutManager: RecyclerView.LayoutManager,
    adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    hasFixedSize: Boolean = false
) {
    this.layoutManager = layoutManager
    this.adapter = adapter
    setHasFixedSize(hasFixedSize)
}


fun FilterChoiceSelected.getSelectedCount(): Int {
    var count = 0
    if (this.isFavorite) {
        count += 1
    }
    if (this.isPriority_red) {
        count += 1
    }
    if (this.isPriority_yellow) {
        count += 1
    }
    if (this.isPriority_green) {
        count += 1
    }
    return count
}



