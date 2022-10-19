package com.example.version2.data.db.respositoryImplmentation

import com.example.version2.data.db.dao.SuggestionDao
import com.example.version2.data.db.mapper.SuggestionsEntityMapperImpl
import com.example.version2.domain.model.SuggestionHistory
import com.example.version2.domain.repository.SuggestionRepository

class SuggestionRepositoryImplmentation(private val suggestionDao: SuggestionDao) : SuggestionRepository {
    override suspend fun getHistory(userId: Int): List<String> {
        return suggestionDao.getHistory(userId)
    }

    override suspend fun deleteHistory(suggestion: String, userId: Int) {
        suggestionDao.deleteHistory(suggestion, userId)
    }

    override suspend fun insertHistory(suggestion: SuggestionHistory,userId: Int): Long {
        return suggestionDao.insertHistory(SuggestionsEntityMapperImpl.toEntity(suggestion,userId))
    }
}

