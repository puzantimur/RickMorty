package com.example.rickandmorty.presentation.ui.character.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.ItemCharactersBinding
import com.example.rickandmorty.databinding.ItemLoadingBinding
import com.example.rickandmorty.domain.Character
import com.example.rickandmorty.presentation.paging.LoadingViewHolder
import com.example.rickandmorty.presentation.paging.PagingDisplayItem

class CharactersAdapter(
    private val onCharacterClicked: (Character) -> Unit
) : ListAdapter<PagingDisplayItem<Character>, RecyclerView.ViewHolder>(DIFF_UTIL) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PagingDisplayItem.Item -> TYPE_CHARACTER
            PagingDisplayItem.Loading -> TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CHARACTER -> {
                CharacterViewHolder(
                    binding = ItemCharactersBinding.inflate(layoutInflater, parent, false),
                    onCharacterClicked = onCharacterClicked,
                )
            }
            TYPE_LOADING -> {
                LoadingViewHolder(
                    binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                )
            }
            else -> {
                error("Unsupported view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val character = (getItem(position) as? PagingDisplayItem.Item)?.data ?: return
        (holder as? CharacterViewHolder)?.bind(character)
    }

    companion object {

        private const val TYPE_CHARACTER = 1
        private const val TYPE_LOADING = 2

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PagingDisplayItem<Character>>() {
            override fun areItemsTheSame(
                oldItem: PagingDisplayItem<Character>,
                newItem: PagingDisplayItem<Character>
            ): Boolean {
                val oldChar = oldItem as? PagingDisplayItem.Item
                val newChar = newItem as? PagingDisplayItem.Item
                return oldChar?.data?.id == newChar?.data?.id
            }

            override fun areContentsTheSame(
                oldItem: PagingDisplayItem<Character>,
                newItem: PagingDisplayItem<Character>
            ): Boolean {
                val oldChar = oldItem as? PagingDisplayItem.Item
                val newChar = newItem as? PagingDisplayItem.Item
                return oldChar == newChar
            }
        }
    }
}