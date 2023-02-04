package com.example.rickandmorty.domain

data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val characters: List<String>
)
