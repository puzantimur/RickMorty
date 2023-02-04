package com.example.rickandmorty.presentation.paging

sealed class PagingDisplayItem<out T> {

    data class Item<T>(val data: T) : PagingDisplayItem<T>()
    object Loading : PagingDisplayItem<Nothing>()
}
