package com.example.rickandmorty.presentation.ui.episode.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.ItemEpisodeBinding
import com.example.rickandmorty.databinding.ItemLoadingBinding
import com.example.rickandmorty.domain.Episode
import com.example.rickandmorty.presentation.paging.LoadingViewHolder
import com.example.rickandmorty.presentation.paging.PagingDisplayItem

class EpisodeAdapter(
    private val onEpisodeClicked: (Episode) -> Unit,
) : ListAdapter<PagingDisplayItem<Episode>, RecyclerView.ViewHolder>(DIFF_UTIL) {

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
                EpisodeViewHolder(
                    binding = ItemEpisodeBinding.inflate(layoutInflater, parent, false),
                    onEpisodeClicked = onEpisodeClicked
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
        (holder as? EpisodeViewHolder)?.bind(episode)
    }

    companion object {

        private const val TYPE_CHARACTER = 1
        private const val TYPE_LOADING = 2

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PagingDisplayItem<Episode>>() {
            override fun areItemsTheSame(
                oldItem: PagingDisplayItem<Episode>,
                newItem: PagingDisplayItem<Episode>
            ): Boolean {
                val oldChar = oldItem as? PagingDisplayItem.Item
                val newChar = newItem as? PagingDisplayItem.Item
                return oldChar?.data?.id == newChar?.data?.id
            }

            override fun areContentsTheSame(
                oldItem: PagingDisplayItem<Episode>,
                newItem: PagingDisplayItem<Episode>
            ): Boolean {
                val oldChar = oldItem as? PagingDisplayItem.Item
                val newChar = newItem as? PagingDisplayItem.Item
                return oldChar == newChar
            }
        }
    }

    inner class EpisodeViewHolder(
        private val binding: ItemEpisodeBinding,
        private val onEpisodeClicked: (Episode) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Episode) {
            with(binding) {
                episodeNumber.text = item.episode
                episodeName.text = item.name
                episodeDate.text = item.date
                root.setOnClickListener {
                    onEpisodeClicked(item)
                }
            }
        }
    }
}
