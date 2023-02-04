package com.example.rickandmorty.presentation.extensions

import android.widget.SearchView
import androidx.appcompat.widget.Toolbar

fun Toolbar.setupToolbar(
    itemId: Int,
    onQueryChanged: (query: String) -> Unit
) {
    this
        .menu
        .findItem(itemId)
        .actionView
        .let { it as SearchView }
        .setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false
                override fun onQueryTextChange(query: String): Boolean {
                    onQueryChanged(query)
                    return true
                }
            }
        )
}
