package com.example.rickandmorty.presentation.ui.location.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.domain.Character
import com.example.rickandmorty.domain.Location
import com.example.rickandmorty.domain.RickAndMortyRepository
import com.example.rickandmorty.presentation.extensions.findId
import com.example.rickandmorty.presentation.model.Lce
import com.example.rickandmorty.presentation.ui.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LocationDetailViewModel @Inject constructor(
    connection: Boolean,
    private val id: Int,
    private val rickAndMortyRepository: RickAndMortyRepository,
) : ViewModel() {

    private var currentIdList: String = ""
    private val currentIdListForDatabase = mutableListOf<Int>()

    private val refreshFlowDetail = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val refreshFlowList = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val detailsFLow =
        if (connection) {
            getDetailsWithInternet()
        } else {
            getDetailsWithoutInternet()
        }

    val charactersInDetailsFlow =
        if (connection) {
            getCharactersListInDetailsWithInternet()
        } else {
            getCharactersListInDetailsWithoutInternet()
        }

    private fun getDetailsWithInternet(): SharedFlow<Lce<Location>> {
        return refreshFlowDetail
            .onStart { refreshFlowDetail.tryEmit(Unit) }
            .map {
                rickAndMortyRepository.getParticularLocation(id)
                    .fold(
                        onSuccess = {
                            it.characters.forEach { string ->
                                currentIdList += "${string.findId()},"
                            }
                            Lce.Content(it)
                        },
                        onFailure = {
                            Lce.Error(it)
                        }
                    )
            }
            .shareIn(
                viewModelScope,
                SharingStarted.Eagerly,
                replay = 1
            )
    }

    private fun getDetailsWithoutInternet(): SharedFlow<Lce<Location>> {
        return refreshFlowDetail
            .onStart { refreshFlowDetail.tryEmit(Unit) }
            .map {
                rickAndMortyRepository.getParticularLocationFromDatabase(id)
                    .fold(
                        onSuccess = {
                            it.characters.forEach { string ->
                                currentIdListForDatabase.add(string.findId().toInt())
                            }
                            Lce.Content(it)
                        },
                        onFailure = {
                            Lce.Error(it)
                        }
                    )
            }
            .shareIn(
                viewModelScope,
                SharingStarted.Eagerly,
                replay = 1
            )
    }

    private fun getCharactersListInDetailsWithInternet(): SharedFlow<Lce<List<Character>>> {
        return refreshFlowList
            .onStart { refreshFlowList.tryEmit(Unit) }
            .debounce(Constants.DEBOUNCE)
            .map {
                rickAndMortyRepository.getPlentyCharacter(currentIdList)
                    .fold(
                        onSuccess = {
                            Lce.Content(it)
                        }, onFailure = {
                            Lce.Error(it)
                        }
                    )
            }
            .shareIn(
                viewModelScope,
                SharingStarted.Eagerly,
                replay = 1
            )
    }

    private fun getCharactersListInDetailsWithoutInternet(): SharedFlow<Lce<List<Character>>> {
        return refreshFlowList
            .onStart { refreshFlowList.tryEmit(Unit) }
            .debounce(Constants.DEBOUNCE)
            .map {
                rickAndMortyRepository.getPlentyCharactersFromDatabase(currentIdListForDatabase)
                    .fold(
                        onSuccess = {
                            Lce.Content(it)
                        }, onFailure = {
                            Lce.Error(it)
                        }
                    )
            }
            .shareIn(
                viewModelScope,
                SharingStarted.Eagerly,
                replay = 1
            )
    }

    fun onRefreshedDetail() {
        refreshFlowDetail.tryEmit(Unit)
    }

    fun onRefreshedList() {
        refreshFlowList.tryEmit(Unit)
    }

    class Factory @AssistedInject constructor(
        @Assisted("connection") private val connection: Boolean,
        @Assisted("idEpisode") private val id: Int,
        private val rickAndMortyRepository: RickAndMortyRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationDetailViewModel(connection, id, rickAndMortyRepository) as T
        }

        @AssistedFactory
        interface InjectFactory {
            fun create(
                @Assisted("connection") connection: Boolean,
                @Assisted("idEpisode") id: Int
            ): Factory
        }
    }
}
