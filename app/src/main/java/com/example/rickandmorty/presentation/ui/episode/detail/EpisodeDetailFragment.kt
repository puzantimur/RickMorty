package com.example.rickandmorty.presentation.ui.episode.detail

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentEpisodeDetailBinding
import com.example.rickandmorty.presentation.appComponent
import com.example.rickandmorty.presentation.extensions.space
import com.example.rickandmorty.presentation.model.BaseFragment
import com.example.rickandmorty.presentation.model.Lce
import com.example.rickandmorty.presentation.paging.PagingDisplayItem
import com.example.rickandmorty.presentation.ui.character.adapter.CharactersAdapter
import com.example.rickandmorty.presentation.ui.character.detail.CharacterDetailFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class EpisodeDetailFragment : BaseFragment<FragmentEpisodeDetailBinding>(
    R.layout.fragment_episode_detail
) {

    @Inject
    lateinit var injectViewModel: EpisodeDetailViewModel.Factory.InjectFactory
    private val viewModel: EpisodeDetailViewModel by viewModels {
        injectViewModel.create(
            checkConnection(),
            requireArguments().getInt(EPISODE_ID)
        )
    }

    private val adapter by lazy {
        CharactersAdapter(
            onCharacterClicked = {
                val detailFragment = CharacterDetailFragment.getInstance(it.id)
                navigateTo(detailFragment)
            }
        )
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(view: View): FragmentEpisodeDetailBinding {
        return FragmentEpisodeDetailBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recyclerViewCharacters.adapter = adapter
            recyclerViewCharacters.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerViewCharacters.space(requireContext())
            addPopBackStackToToolbar(toolbar)
            setupView()
            swipeRefresh.setOnRefreshListener {
                viewModel.onRefreshed()
            }
        }
    }

    private fun setupView() {
        with(binding) {
            viewModel
                .detailsFLow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { swipeRefresh.isRefreshing = false }
                .onEach { lce ->
                    when (lce) {
                        is Lce.Content -> {
                            episodeDate.isVisible = true
                            episodeDate.text = lce.data.date
                            episodeName.isVisible = true
                            episodeName.text = lce.data.name
                            episodeNumber.isVisible = true
                            episodeNumber.text = lce.data.episode
                        }
                        is Lce.Error -> {
                            progress.isVisible = false
                            Toast.makeText(
                                requireContext(),
                                lce.throwable.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Lce.Loading -> {}
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)

            viewModel
                .charactersInDetailsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { swipeRefresh.isRefreshing = false }
                .onEach { lce ->
                    when (lce) {
                        is Lce.Content -> {
                            progress.isVisible = false
                            recyclerViewCharacters.isVisible = true
                            adapter.submitList(lce.data.map { PagingDisplayItem.Item(it) })
                        }
                        is Lce.Error -> {
                            Toast.makeText(
                                requireContext(),
                                lce.throwable.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Lce.Loading -> {}
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
        }

    }

    companion object {
        const val EPISODE_ID = "EPISODE_ID"

        fun getInstance(
            id: Int
        ): EpisodeDetailFragment {
            return EpisodeDetailFragment().apply {
                arguments = bundleOf(
                    EPISODE_ID to id
                )

            }
        }
    }
}
