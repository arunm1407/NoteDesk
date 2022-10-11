package com.example.notedesk.data.data_source

sealed interface DbDao
{
    object NotesDao : DbDao
    object UserDao : DbDao





}