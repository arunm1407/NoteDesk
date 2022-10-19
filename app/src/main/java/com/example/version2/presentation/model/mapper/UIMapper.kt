package com.example.version2.presentation.model.mapper

interface UIMapper<E,V> {

    fun mapToView(input: E): V
    fun viewToDomain(input: V):E
}