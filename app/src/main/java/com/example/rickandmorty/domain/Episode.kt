package com.example.rickandmorty.domain

data class Episode(
    val id: Int,
    val name: String,
    val date: String,
    val episode: String,
    val characters: List<String>
)
