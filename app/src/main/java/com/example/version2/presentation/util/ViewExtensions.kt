package com.example.version2.presentation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.version2.R
import com.example.version2.domain.model.Gender
import com.example.version2.presentation.homeScreen.enums.FilterChoiceSelected
import com.google.android.material.textfield.TextInputLayout
import java.util.*


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


fun TextView.set(name: String) {
    this.text = name
}


fun EditText.checkNotEmpty(): Boolean {
    return this.text.toString().trim().isNotEmpty()
}


fun String.checkEmpty(): Boolean {
    return this.trim().isEmpty()
}

fun EditText.getString(): String {
    return this.text.trim().toString()
}


fun EditText.getPinCode(): Int {

    return if (this.text.trim().isBlank()) 0
    else this.getString().toInt()

}

fun EditText.getStringLower(): String {
    return this.text.trim().toString().lowercase(Locale.ROOT)
}

fun TextInputLayout.clearError() {
    this.error = null
}


fun EditText.clearText() {
    this.text.clear()
}


fun TextInputLayout.setErrorMessage(error: String?) {
    this.error = error
}


fun Toolbar.setup(activity: Activity, name: String) {

    menu.clear()
    (activity as AppCompatActivity).apply {
        this.title = name
        this.setSupportActionBar(this@setup)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
    this.navigationIcon =
        ContextCompat.getDrawable(activity, R.drawable.ic_baseline_arrow_back_24)
}


fun Toolbar.updateTitle(name: String) {
    this.title = name
}

fun TextView.getString(): String {
    return this.text.trim().toString()
}


fun TextView.checkEmpty(): Boolean {
    return this.text.isEmpty()
}

inline fun FragmentManager.inTransaction(
    name: String?,
    func: FragmentTransaction.() -> FragmentTransaction
) {

    beginTransaction().apply {
        setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        addToBackStack(name)
        func().commit()

    }

}


fun String.getGender(): Gender {


    return when (this) {
        "MEN" -> Gender.MEN
        "WOMEN" -> Gender.WOMEN
        else -> Gender.NOT_SPECIFIED
    }
}


inline fun <T : Fragment> T.withArgs(args: Bundle.() -> Unit): T =
    this.apply {
        arguments = Bundle().apply(args)

    }


fun Context.openActivity(it: Intent, args: Bundle.() -> Unit = {}) {

    it.putExtras(Bundle().apply(args))
    startActivity(it)
}


fun <T> Context.startActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}


fun ImageView.setColor(color: Int) {
    this.setColorFilter(
        ContextCompat.getColor(
            context,
            color
        )
    )
}


fun String.toColor(): Int {
    return Color.parseColor(this)
}


fun TextView.actionDone() {
    this.setOnEditorActionListener { v, actionId, _ ->
        return@setOnEditorActionListener when (actionId) {
            EditorInfo.IME_ACTION_DONE -> {
                v.clearFocus()
                v.hideKeyboard()
                true
            }
            else -> false
        }
    }
}


fun EditText.clearErrorOnClick(view: TextInputLayout) {
    this.setOnClickListener {

        view.clearError()
    }
}

fun Int.toPincode(): String {
    return if (this == 0) "" else this.toString()
}
