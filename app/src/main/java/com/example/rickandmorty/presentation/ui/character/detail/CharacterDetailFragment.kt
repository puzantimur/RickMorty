package com.example.rickandmorty.presentation.ui.character.detail

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
import coil.load
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmorty.presentation.appComponent
import com.example.rickandmorty.presentation.extensions.findId
import com.example.rickandmorty.presentation.extensions.space
import com.example.rickandmorty.presentation.model.BaseFragment
import com.example.rickandmorty.presentation.model.Lce
import com.example.rickandmorty.presentation.paging.PagingDisplayItem
import com.example.rickandmorty.presentation.ui.episode.adapter.EpisodeAdapter
import com.example.rickandmorty.presentation.ui.episode.detail.EpisodeDetailFragment
import com.example.rickandmorty.presentation.ui.location.detail.LocationDetailFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CharacterDetailFragment : BaseFragment<FragmentCharacterDetailsBinding>(
    R.layout.fragment_character_details
) {

    @Inject
    lateinit var injectViewModel: CharacterDetailViewModel.Factory.InjectFactory
    private val viewModel: CharacterDetailViewModel by viewModels {
        injectViewModel.create(checkConnection(), requireArguments().getInt(CHARACTER_ID))
    }

    private val adapter by lazy {
        EpisodeAdapter(
            onEpisodeClicked = {
                val episodeDetailFragment = EpisodeDetailFragment.getInstance(it.id)
                navigateTo(episodeDetailFragment)
            }
        )
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(view: View): FragmentCharacterDetailsBinding {
        return FragmentCharacterDetailsBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recyclerViewEpisodes.adapter = adapter
            recyclerViewEpisodes.space(requireContext())
            addPopBackStackToToolbar(binding.toolbar)
            setupView()
            swipeRefresh.setOnRefreshListener {
                viewModel.onRefreshed()
            }
        }
    }

    private fun setupView() {
        with(binding) {
            viewModel
                .detailsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { swipeRefresh.isRefreshing = false }
                .onEach { lce ->
                    when (lce) {
                        is Lce.Content -> {
                            image.load(lce.data.image) {
                                placeholder(R.drawable.placeholder)
                            }
                            characterName.isVisible = true
                            characterName.text = lce.data.name
                            gender.isVisible = true
                            characterGender.isVisible = true
                            characterGender.text = lce.data.gender
                            species.isVisible = true
                            recyclerViewEpisodes.isVisible = true
                            characterSpecies.isVisible = true
                            characterSpecies.text = lce.data.species
                            status.isVisible = true
                            characterStatus.isVisible = true
                            characterStatus.text = lce.data.status
                            origin.isVisible = true
                            characterOrigin.text = lce.data.origin.name
                            if (lce.data.origin.name != "unknown") {
                                characterOrigin.setOnClickListener {
                                    navigateTo(
                                        LocationDetailFragment.getInstance(
                                            lce.data.origin.url.findId().toInt()
                                        )
                                    )
                                }
                            }
                            if (lce.data.location.name != "unknown") {
                                characterLocation.setOnClickListener {
                                    navigateTo(
                                        LocationDetailFragment.getInstance(
                                            lce.data.location.url.findId().toInt()
                                        )
                                    )
                                }
                            }
                            characterOrigin.isVisible = true
                            location.isVisible = true
                            characterLocation.text = lce.data.location.name
                            characterLocation.isVisible = true
                            episodes.isVisible = true
                        }
                        is Lce.Error -> {
                            progress.isVisible = false
                            Toast.makeText(
                                requireContext(),
                                R.string.check_connection,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Lce.Loading -> {}
                    }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

            viewModel
                .episodesInDetailsFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { swipeRefresh.isRefreshing = false }
                .onEach { lce ->
                    when (lce) {
                        is Lce.Content -> {
                            progress.isVisible = false
                            if (lce.data.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.check_connection,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                recyclerViewEpisodes.isVisible = true
                                adapter.submitList(lce.data.map { PagingDisplayItem.Item(it) })
                            }

                        }
                        is Lce.Error -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.check_connection,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Lce.Loading -> {}
                    }

                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    companion object {
        const val CHARACTER_ID = "CHARACTER_ID"

        fun getInstance(
            id: Int
        ): CharacterDetailFragment {
            return CharacterDetailFragment().apply {
                arguments = bundleOf(
                    CHARACTER_ID to id
                )

            }
        }
    }
}
