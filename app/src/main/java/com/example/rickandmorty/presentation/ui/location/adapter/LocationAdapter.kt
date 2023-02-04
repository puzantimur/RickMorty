package com.example.rickandmorty.presentation.ui.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.ItemLoadingBinding
import com.example.rickandmorty.databinding.ItemLocationBinding
import com.example.rickandmorty.domain.Location
import com.example.rickandmorty.presentation.paging.LoadingViewHolder
import com.example.rickandmorty.presentation.paging.PagingDisplayItem

class LocationAdapter(
    private val onLocationClicked: (Location) -> Unit,
) : ListAdapter<PagingDisplayItem<Location>, RecyclerView.ViewHolder>(DIFF_UTIL) {

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
                LocationViewHolder(
                    binding = ItemLocationBinding.inflate(layoutInflater, parent, false),
                    onLocationClicked = onLocationClicked
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
        val episode = (getItem(position) as? PagingDisplayItem.Item)?.data ?: return
        (holder as? LocationViewHolder)?.bind(episode)
    }

    companion object {

        private const val TYPE_CHARACTER = 1
        private const val TYPE_LOADING = 2

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PagingDisplayItem<Location>>() {
            override fun areItemsTheSame(
                oldItem: PagingDisplayItem<Location>,
                newItem: PagingDisplayItem<Location>
            ): Boolean {
                val oldChar = oldItem as? PagingDisplayItem.Item
                val newChar = newItem as? PagingDisplayItem.Item
                return oldChar?.data?.id == newChar?.data?.id
            }

            override fun areContentsTheSame(
                oldItem: PagingDisplayItem<Location>,
                newItem: PagingDisplayItem<Location>
            ): Boolean {
                val oldChar = oldItem as? PagingDisplayItem.Item
                val newChar = newItem as? PagingDisplayItem.Item
                return oldChar == newChar
            }
        }
    }

    inner class LocationViewHolder(
        private val binding: ItemLocationBinding,
        private val onLocationClicked: (Location) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Location) {
            with(binding) {
                locationName.text = item.name
                locationDimens.text = item.dimension
                locationType.text = item.type
                root.setOnClickListener {
                    onLocationClicked(item)
                }
            }
        }
    }
}
