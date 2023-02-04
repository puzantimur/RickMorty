package com.example.rickandmorty.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val episode: List<String>,
    val origin: CharacterLocationOriginEntity,
    val location: CharacterLocationOriginEntity
)
