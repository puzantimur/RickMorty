package com.example.rickandmorty.presentation.ui.episode

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentEpisodeBinding
import com.example.rickandmorty.presentation.appComponent
import com.example.rickandmorty.presentation.extensions.setupGridLayoutManager
import com.example.rickandmorty.presentation.extensions.setupToolbar
import com.example.rickandmorty.presentation.model.BaseFragment
import com.example.rickandmorty.presentation.model.FilterEpisode
import com.example.rickandmorty.presentation.paging.PagingDisplayItem
import com.example.rickandmorty.presentation.ui.episode.adapter.EpisodeAdapter
import com.example.rickandmorty.presentation.ui.episode.detail.EpisodeDetailFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class EpisodeFragment : BaseFragment<FragmentEpisodeBinding>(
    R.layout.fragment_episode
) {

    @Inject
    lateinit var injectViewModel: EpisodeViewModel.Factory.InjectFactory
    private val viewModel: EpisodeViewModel by viewModels {
        injectViewModel.create(checkConnection())
    }

    private val code by lazy { resources.getStringArray(R.array.code) }
    private val codeAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.filter_item, code)
    }

    private val adapter by lazy {
        EpisodeAdapter(
            onEpisodeClicked = {
                val detailEpisode = EpisodeDetailFragment.getInstance(it.id)
                navigateToDetail(detailEpisode)
            }
        )
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(view: View): FragmentEpisodeBinding {
        return FragmentEpisodeBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setupView()
    }

    private fun setupWidgets() {
        with(binding) {
            recyclerViewEpisodes.setupGridLayoutManager(
                itemsToLoad = ITEMS_TO_LOAD,
                requireContext(),
                adapter
            ) {
                viewModel.onLoadMore()
            }

            toolbar.setupToolbar(
                R.id.action_search,
            ) {
                viewModel.onQueryChanged(FilterEpisode(name = it))
            }

            toolbar.setNavigationOnClickListener {
                setupDialogFilter()
            }

            swipeRefresh.setOnRefreshListener {
                viewModel.onRefreshed()
            }
        }
    }

    private fun setupView() {
        with(binding) {
            viewModel
                .dataFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { swipeRefresh.isRefreshing = false }
                .onEach { list ->
                    if (list.isEmpty() && checkConnection()) {
                        adapter.submitList(list.map { PagingDisplayItem.Item(it) })
                        progress.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.check_connection,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (list.isEmpty()) {
                        adapter.submitList(list.map { PagingDisplayItem.Item(it) })
                        progress.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.no_episode,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        progress.isVisible = false
                        recyclerViewEpisodes.isVisible = true
                        if (list.size < MAX_COUNT_ITEMS && checkConnection()) {
                            adapter.submitList(list.map { PagingDisplayItem.Item(it) }
                                .plus(PagingDisplayItem.Loading))
                        } else {
                            adapter.submitList(list.map { PagingDisplayItem.Item(it) })
                        }
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun navigateToDetail(fragment: EpisodeDetailFragment) {
        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.to_left_in,
                R.anim.to_left_out,
                R.anim.to_right_in,
                R.anim.to_right_out
            )
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupDialogFilter() {
        val dialog = BottomSheetDialog(
            requireContext()
        )
        val bottomSheetView = LayoutInflater.from(requireContext()).inflate(
            R.layout.filter_episode, null
        )
        bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_code)
            .setAdapter(codeAdapter)
        bottomSheetView.findViewById<TextView>(R.id.done).setOnClickListener {
            val filterEpisode = FilterEpisode(
                code =
                bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_code).text
                    .toString()
            )
            viewModel.onQueryChanged(filterEpisode)
            dialog.dismiss()
        }
        bottomSheetView.findViewById<ImageView>(R.id.close).setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheetView)
        dialog.show()
    }

    companion object {
        private const val ITEMS_TO_LOAD = 10
        private const val MAX_COUNT_ITEMS = 51
    }
}
