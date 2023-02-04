package com.example.rickandmorty.data.api.model

data class CharacterDTO(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val episode: List<String>,
    val origin: CharacterLocationOriginDTO,
    val location: CharacterLocationOriginDTO
)
