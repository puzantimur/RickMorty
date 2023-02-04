package com.example.rickandmorty.domain

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val episode: List<String>,
    val origin: CharacterLocationOrigin,
    val location: CharacterLocationOrigin
)
