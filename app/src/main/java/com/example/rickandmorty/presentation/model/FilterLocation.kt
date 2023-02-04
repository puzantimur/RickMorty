package com.example.rickandmorty.presentation.model

import com.example.rickandmorty.presentation.ui.Constants

data class FilterLocation(
    val name: String = Constants.EMPTY,
    val type: String = Constants.EMPTY,
    val dimension: String = Constants.EMPTY
)
