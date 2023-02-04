package com.example.rickandmorty.presentation.ui.character.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ItemCharactersBinding
import com.example.rickandmorty.domain.Character

class CharacterViewHolder(
    private val binding: ItemCharactersBinding,
    private val onCharacterClicked: (Character) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Character) {
        with(binding) {
            characterName.text = item.name
            characterGender.text = item.gender
            characterImage.load(item.image) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.placeholder)
            }
            characterSpecies.text = item.species
            characterStatus.text = item.status
            root.setOnClickListener {
                onCharacterClicked(item)
            }
        }
    }
}
