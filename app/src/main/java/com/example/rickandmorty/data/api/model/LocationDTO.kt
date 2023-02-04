package com.example.rickandmorty.data.api.model

import com.google.gson.annotations.SerializedName

data class LocationDTO(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    @SerializedName("residents")
    val characters: List<String>
)
