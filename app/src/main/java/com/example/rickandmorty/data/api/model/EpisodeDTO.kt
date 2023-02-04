package com.example.rickandmorty.data.api.model

import com.google.gson.annotations.SerializedName

data class EpisodeDTO(
    val id: Int,
    val name: String,
    @SerializedName("air_date")
    val date: String,
    val episode: String,
    val characters: List<String>
)
