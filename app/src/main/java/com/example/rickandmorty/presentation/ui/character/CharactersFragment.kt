package com.example.rickandmorty.presentation.ui.character

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
import com.example.rickandmorty.databinding.FragmentCharactersBinding
import com.example.rickandmorty.presentation.appComponent
import com.example.rickandmorty.presentation.extensions.setupGridLayoutManager
import com.example.rickandmorty.presentation.extensions.setupToolbar
import com.example.rickandmorty.presentation.model.BaseFragment
import com.example.rickandmorty.presentation.model.FilterCharacter
import com.example.rickandmorty.presentation.paging.PagingDisplayItem
import com.example.rickandmorty.presentation.ui.character.adapter.CharactersAdapter
import com.example.rickandmorty.presentation.ui.character.detail.CharacterDetailFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CharactersFragment : BaseFragment<FragmentCharactersBinding>(
    R.layout.fragment_characters
) {

    @Inject
    lateinit var injectViewModel: CharactersViewModel.Factory.InjectFactory
    private val viewModel: CharactersViewModel by viewModels {
        injectViewModel.create(checkConnection())
    }

    private val status by lazy { resources.getStringArray(R.array.status) }
    private val statusAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.filter_item, status)
    }

    private val species by lazy { resources.getStringArray(R.array.species) }
    private val speciesAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.filter_item, species)
    }

    private val gender by lazy { resources.getStringArray(R.array.gender) }
    private val genderAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.filter_item, gender)
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

    override fun createBinding(view: View): FragmentCharactersBinding {
        return FragmentCharactersBinding.bind(view)
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
                            R.string.no_char,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        progress.isVisible = false
                        recyclerViewCharacters.isVisible = true
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
            recyclerViewCharacters.setupGridLayoutManager(
                itemsToLoad = ITEMS_TO_LOAD,
                requireContext(),
                adapter
            ) {
                viewModel.onLoadMore()
            }

            toolbar.setupToolbar(
                R.id.action_search,
            ) {
                viewModel.onQueryChanged(FilterCharacter(name = it))
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
            R.layout.filter_character, null
        )
        bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_status)
            .setAdapter(statusAdapter)
        bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_species)
            .setAdapter(speciesAdapter)
        bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_gender)
            .setAdapter(genderAdapter)
        bottomSheetView.findViewById<TextView>(R.id.done).setOnClickListener {
            val filterCharacter = FilterCharacter(
                status =
                bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_status)
                    .text.toString(),
                species =
                bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_species)
                    .text.toString(),
                gender =
                bottomSheetView.findViewById<AutoCompleteTextView>(R.id.auto_complete_gender)
                    .text.toString(),
            )
            viewModel.onQueryChanged(filterCharacter)
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
        private const val MAX_COUNT_ITEMS = 826
    }
}
