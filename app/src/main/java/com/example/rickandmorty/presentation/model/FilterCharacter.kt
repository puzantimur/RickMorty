package com.example.rickandmorty.presentation.model

import com.example.rickandmorty.presentation.ui.Constants

data class FilterCharacter(
    val name: String = Constants.EMPTY,
    val status: String = Constants.EMPTY,
    val species: String = Constants.EMPTY,
    val gender: String = Constants.EMPTY
)
