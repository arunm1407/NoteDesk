package com.example.version2.domain.repository

import com.example.version2.domain.model.SuggestionHistory

interface SuggestionRepository {

    suspend fun getHistory(userId: Int): List<String>
    suspend fun deleteHistory(suggestion: String, userId: Int)
    suspend fun insertHistory(suggestion: SuggestionHistory, userId: Int): Long


}