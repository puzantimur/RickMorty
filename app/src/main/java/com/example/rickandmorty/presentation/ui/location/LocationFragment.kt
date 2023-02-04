package com.example.rickandmorty.presentation.ui.location

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
import com.example.rickandmorty.databinding.FragmentLocationBinding
import com.example.rickandmorty.presentation.appComponent
import com.example.rickandmorty.presentation.extensions.setupGridLayoutManager
import com.example.rickandmorty.presentation.extensions.setupToolbar
import com.example.rickandmorty.presentation.model.BaseFragment
import com.example.rickandmorty.presentation.model.FilterLocation
import com.example.rickandmorty.presentation.paging.PagingDisplayItem
import com.example.rickandmorty.presentation.ui.location.adapter.LocationAdapter
import com.example.rickandmorty.presentation.ui.location.detail.LocationDetailFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class LocationFragment : BaseFragment<FragmentLocationBinding>(
    R.layout.fragment_location
) {

    @Inject
    lateinit var injectViewModel: LocationViewModel.Factory.InjectFactory
    private val viewModel: LocationViewModel by viewModels {
        injectViewModel.create(checkConnection())
    }

    private val type by lazy { resources.getStringArray(R.array.type) }
    private val typeAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.filter_item, type)
    }

    private val dimension by lazy { resources.getStringArray(R.array.dimension) }
    private val dimensionAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.filter_item, dimension)
    }

    private val adapter by lazy {
        LocationAdapter(
            onLocationClicked = {
                val detailFragment = LocationDetailFragment.getInstance(it.id)
                navigateTo(detailFragment)
            }
        )
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(view: View): FragmentLocationBinding {
        return FragmentLocationBinding.bind(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setupView()
    }

    private fun setupView() {
        with(binding) {
            viewModel
                .dataFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { swipeRefresh.isRefreshing = false }
                .onEach { list ->
                    if (list.isEmpty() && !checkConnection()) {
                        adapter.submitList(list.map { PagingDisplayItem.Item(it) })
                        progress.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.check_connection,
                            Toast.LENGTH_LONG
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
                        recyclerViewLocations.isVisible = true
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

    private fun setupWidgets() {
        with(binding) {
            recyclerViewLocations.setupGridLayoutManager(
                itemsToLoad = ITEMS_TO_LOAD,
                requireContext(),
                adapter
            ) {
                viewModel.onLoadMore()
            }

            toolbar.setupToolbar(
                R.id.action_search,
            ) {
                viewModel.onQueryChanged(FilterLocation(name = it))
            }

            toolbar.setNavigationOnClickListener {
                setupDialogFilter()
            }

            swipeRefresh.setOnRefreshListener {
                viewModel.onRefreshed()
            }
        }
    }

    private fun setupDialogFilter() {
        val dialog = BottomSheetDialog(
            requireContext()
        )
        val bottomSheetView = LayoutInflater.from(requireContext()).inflate(
            R.layout.filter_location, null
        )
        bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_type)
            .setAdapter(typeAdapter)
        bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_dimension)
            .setAdapter(dimensionAdapter)
        bottomSheetView.findViewById<TextView>(R.id.done).setOnClickListener {
            val filterLocation = FilterLocation(
                type =
                bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_type)
                    .text.toString(),
                dimension =
                bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_dimension)
                    .text.toString()
            )
            viewModel.onQueryChanged(filterLocation)
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
        private const val MAX_COUNT_ITEMS = 126
    }
}
