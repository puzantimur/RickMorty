package com.example.rickandmorty.presentation.model

import com.example.rickandmorty.presentation.ui.Constants

data class FilterEpisode(
    val name: String = Constants.EMPTY,
    val code: String = Constants.EMPTY
)
