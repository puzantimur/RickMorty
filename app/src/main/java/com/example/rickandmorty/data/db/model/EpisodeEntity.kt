package com.example.rickandmorty.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EpisodeEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val date: String,
    val episode: String,
    val characters: List<String>
)
