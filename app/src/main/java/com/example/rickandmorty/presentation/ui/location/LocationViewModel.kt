package com.example.rickandmorty.presentation.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.domain.Location
import com.example.rickandmorty.domain.RickAndMortyRepository
import com.example.rickandmorty.presentation.model.FilterLocation
import com.example.rickandmorty.presentation.ui.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val connection: Boolean,
    private val rickAndMortyRepository: RickAndMortyRepository
) : ViewModel() {

    private var isLoading = false
    private var currentPage = 1

    private val queryFlow = MutableStateFlow(FilterLocation())

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
            getLocationWithInternet()
        } else {
            getLocationsWithoutInternet()
        }

    private fun getLocationWithInternet(): SharedFlow<List<Location>> {
        return queryFlow
            .debounce(Constants.DEBOUNCE)
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
                    }.onStart {
                        rickAndMortyRepository.getAllLocationsFromDatabase().onSuccess {
                            if (it.isNotEmpty()) {
                                emit(it)
                            } else {
                                rickAndMortyRepository.getFilteredLocation(
                                    type = query.type,
                                    dimension = query.dimension,
                                    name = query.name,
                                    currentPage
                                )
                                    .fold(
                                        onSuccess = { list ->
                                            rickAndMortyRepository.insertLocationsInDatabase(it)
                                            emit(list)
                                        },
                                        onFailure = {
                                            emptyList<Location>()
                                        }
                                    )
                            }
                        }.onFailure {
                            emit(emptyList<Location>())
                        }
                    }
                    .map {
                        rickAndMortyRepository.getFilteredLocation(
                            type = query.type,
                            dimension = query.dimension,
                            name = query.name,
                            currentPage
                        )
                            .fold(
                                onSuccess = {
                                    rickAndMortyRepository.insertLocationsInDatabase(it)
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

    private fun getLocationsWithoutInternet(): SharedFlow<List<Location>> {
        return queryFlow
            .onStart { refreshFlow.tryEmit(Unit) }
            .combine(refreshFlow.map {
                rickAndMortyRepository.getAllLocationsFromDatabase()
            }) { query, result ->
                result.fold(onSuccess = {
                    it.filter { location ->
                        location.name.contains(query.name, ignoreCase = true) &&
                                location.type.contains(query.type) &&
                                location.dimension.contains(query.dimension)
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

    fun onQueryChanged(query: FilterLocation) {
        queryFlow.value = query
    }

    enum class LoadItemsType {
        REFRESH, LOAD_MORE
    }


    class Factory @AssistedInject constructor(
        @Assisted("connection") private val connection: Boolean,
        private val rickAndMortyRepository: RickAndMortyRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationViewModel(connection, rickAndMortyRepository) as T
        }

        @AssistedFactory
        interface InjectFactory {
            fun create(@Assisted("connection") connection: Boolean): Factory
        }
    }
}
