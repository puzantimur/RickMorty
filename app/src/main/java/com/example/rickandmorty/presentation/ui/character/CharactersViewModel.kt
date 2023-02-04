package com.example.rickandmorty.presentation.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.domain.Character
import com.example.rickandmorty.domain.RickAndMortyRepository
import com.example.rickandmorty.presentation.model.FilterCharacter
import com.example.rickandmorty.presentation.ui.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CharactersViewModel @Inject constructor(
    private val connection: Boolean,
    private val rickAndMortyRepository: RickAndMortyRepository,
) : ViewModel() {

    private var isLoading = false
    private var currentPage = 1

    private val queryFlow = MutableStateFlow(FilterCharacter())

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
            getCharactersWithInternet()
        } else {
            getCharactersWithoutInternet()
        }

    private fun getCharactersWithInternet(): SharedFlow<List<Character>> {
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
                        rickAndMortyRepository.getAllCharactersFromDatabase().onSuccess {
                            if (it.isNotEmpty()) {
                                emit(it)
                            } else {
                                rickAndMortyRepository.getFilteredCharacters(
                                    status = query.status,
                                    species = query.species,
                                    gender = query.gender,
                                    name = query.name,
                                    page = currentPage
                                )
                                    .fold(
                                        onSuccess = { list ->
                                            rickAndMortyRepository.insertCharactersInDatabase(list)
                                            emit(list)
                                        },
                                        onFailure = {
                                            emit(emptyList<Character>())
                                        }
                                    )
                            }
                        }.onFailure {
                            emit(emptyList<Character>())
                        }
                    }
                    .map {
                        rickAndMortyRepository.getFilteredCharacters(
                            status = query.status,
                            species = query.species,
                            gender = query.gender,
                            name = query.name,
                            page = currentPage
                        )
                            .fold(
                                onSuccess = {
                                    rickAndMortyRepository.insertCharactersInDatabase(it)
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

    private fun getCharactersWithoutInternet(): SharedFlow<List<Character>> {
        return queryFlow
            .onStart { refreshFlow.tryEmit(Unit) }
            .combine(refreshFlow.map {
                rickAndMortyRepository.getAllCharactersFromDatabase()
            }) { query, result ->
                result.fold(onSuccess = {
                    it.filter { character ->
                        character.name.contains(query.name, ignoreCase = true) &&
                                character.gender.contains(query.gender) &&
                                character.species.contains(query.species) &&
                                character.status.contains(query.status)
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

    fun onQueryChanged(query: FilterCharacter) {
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
            return CharactersViewModel(connection, rickAndMortyRepository) as T
        }

        @AssistedFactory
        interface InjectFactory {

            fun create(@Assisted("connection") connection: Boolean): Factory
        }
    }
}
