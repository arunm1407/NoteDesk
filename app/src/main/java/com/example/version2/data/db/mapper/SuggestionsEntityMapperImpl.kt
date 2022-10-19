package com.example.version2.data.db.mapper

import com.example.version2.data.db.entity.DbSuggestionHistory
import com.example.version2.domain.model.SuggestionHistory

object SuggestionsEntityMapperImpl : SuggestionEntityMapper<DbSuggestionHistory> {
    override fun fromEntity(entity: DbSuggestionHistory): SuggestionHistory {
        return SuggestionHistory(entity.timeStamp, entity.suggestion)
    }

    override fun toEntity(model: SuggestionHistory, userId: Int): DbSuggestionHistory {
        return DbSuggestionHistory(
            userId, model.timeStamp, model.suggestion

        )
    }
}