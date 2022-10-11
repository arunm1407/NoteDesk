package com.example.notedesk.presentation.home.enums

import java.io.Serializable


data class FilterChoiceSelected(
    val isFavorite: Boolean,
    val isPriority_red: Boolean,
    val isPriority_yellow: Boolean,
    val isPriority_green: Boolean
) :Serializable


