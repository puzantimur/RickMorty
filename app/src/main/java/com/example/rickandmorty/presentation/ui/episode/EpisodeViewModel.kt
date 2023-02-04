package com.example.rickandmorty.presentation.ui.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.domain.Episode
import com.example.rickandmorty.domain.RickAndMortyRepository
import com.example.rickandmorty.presentation.model.FilterEpisode
import com.example.rickandmorty.presentation.ui.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class EpisodeViewModel @Inject constructor(
    private val connection: Boolean,
    private val rickAndMortyRepository: RickAndMortyRepository
) : ViewModel() {

    private var isLoading = false
    private var currentPage = 1

    private val queryFlow = MutableStateFlow(FilterEpisode())

    private val loadItemsFlow = MutableSharedFlow<LoadItemsType>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val refreshFlow = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val dataFlow =
        if (connection) {
            getEpisodesWithInternet()
        } else {
            getEpisodesWithoutInternet()
        }

    private fun getEpisodesWithInternet(): SharedFlow<List<Episode>> {
        return queryFlow.debounce(Constants.DEBOUNCE)
            .flatMapLatest { query ->
                loadItemsFlow
                    .onEach { isLoading = true }
                    .map { loadType ->
                        when (loadType) {
                            LoadItemsType.REFRESH -> {
                                currentPage = 0
                            }
                            LoadItemsType.LOAD_MORE -> {
                                currentPage++
                            }
                        }
                    }
                    .onStart {
                        rickAndMortyRepository.getAllEpisodesFromDatabase().onSuccess {
                            if (it.isNotEmpty()) {
                                emit(it)
                            } else {
                                rickAndMortyRepository.getFilteredEpisode(
                                    code = query.code,
                                    name = query.name,
                                    currentPage
                                )
                                    .fold(
                                        onSuccess = { list ->
                                            rickAndMortyRepository.insertEpisodesInDatabase(list)
                                            emit(list)
                                        },
                                        onFailure = {
                                            emptyList<Episode>()
                                        }
                                    )
                            }
                        }.onFailure {
                            emit(emptyList<Episode>())
                        }
                    }.map {
                        rickAndMortyRepository.getFilteredEpisode(
                            code = query.code,
                            name = query.name,
                            currentPage
                        )
                            .fold(
                                onSuccess = {
                                    rickAndMortyRepository.insertEpisodesInDatabase(it)
                                    it
                                },
                                onFailure = {
                                    emptyList()
                                }
                            )
                    }
                    .onEach { isLoading = false }
                    .runningReduce { items, loadedItems ->
                        items.union(loadedItems).toList()
                    }
            }
            .shareIn(
                viewModelScope,
                SharingStarted.Eagerly,
                replay = 1
            )
    }

    private fun getEpisodesWithoutInternet(): SharedFlow<List<Episode>> {
        return queryFlow
            .onStart { refreshFlow.tryEmit(Unit) }
            .combine(refreshFlow.map {
                rickAndMortyRepository.getAllEpisodesFromDatabase()
            }) { query, result ->
                result.fold(
                    onSuccess = {
                        it.filter { character ->
                            character.name.contains(query.name, ignoreCase = true) &&
                                    character.episode.contains(query.code)
                        }
                    }, onFailure = {
                        emptyList()
                    }
                )
            }
            .shareIn(
                viewModelScope,
                SharingStarted.Eagerly,
                replay = 1
            )
    }

    fun onLoadMore() {
        if (!isLoading) {
            loadItemsFlow.tryEmit(LoadItemsType.LOAD_MORE)
        }
    }

    fun onRefreshed() {
        if (connection) {
            loadItemsFlow.tryEmit(LoadItemsType.REFRESH)
        } else {
            refreshFlow.tryEmit(Unit)
        }
    }

    fun onQueryChanged(query: FilterEpisode) {
        queryFlow.value = query
    }

    private enum class LoadItemsType {
        REFRESH, LOAD_MORE
    }


    class Factory @AssistedInject constructor(
        @Assisted("connection") private val connection: Boolean,
        private val rickAndMortyRepository: RickAndMortyRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EpisodeViewModel(connection, rickAndMortyRepository) as T
        }

        @AssistedFactory
        interface InjectFactory {

            fun create(@Assisted("connection") connection: Boolean): Factory
        }
    }
}
