package com.example.rickandmorty.presentation.ui.location

import com.example.rickandmorty.data.repository.RickAndMortyRepositoryImpl
import com.example.rickandmorty.domain.Character
import com.example.rickandmorty.domain.CharacterLocationOrigin
import com.example.rickandmorty.domain.Location
import com.example.rickandmorty.presentation.model.Lce
import com.example.rickandmorty.presentation.ui.location.detail.LocationDetailViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LocationDetailViewModelTest : ViewModelTest() {

    @Test
    fun `loading detail with internet`() = runTest {
        val mockRepository = getMockkDetailLocationAndListWithInternet()
        val viewModel = LocationDetailViewModel(true, 1, mockRepository)
        viewModel.onRefreshedDetail()
        val actual = viewModel.detailsFLow.first()
        val expected = Lce.Content(
            Location(
                id = 1,
                name = "name",
                type = "type",
                dimension = "dimension",
                characters = List(10) { characters ->
                    "character $characters"
                })
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `loading list with internet`() = runTest {
        val mockRepository = getMockkDetailLocationAndListWithInternet()
        val viewModel = LocationDetailViewModel(true, 1, mockRepository)
        viewModel.onRefreshedList()
        val actual = viewModel.charactersInDetailsFlow.first()
        val characters = List(5) {
            Character(
                id = it,
                name = "{$it name}",
                status = "{$it status}",
                species = "{$it species}",
                gender = "{$it gender}",
                image = "{$it image}",
                episode = List(5) { episode ->
                    episode.toString()
                },
                origin = CharacterLocationOrigin("$it name origin", "$it url"),
                location = CharacterLocationOrigin("$it name origin", "$it url")
            )
        }
        val expected = Lce.Content(characters)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `loading detail without internet`() = runTest {
        val mockRepository = getMockkDetailLocationAndListWithoutInternet()
        val viewModel = LocationDetailViewModel(false, 1, mockRepository)
        viewModel.onRefreshedDetail()
        val actual = viewModel.detailsFLow.first()
        val expected = Lce.Content(
            Location(
                id = 1,
                name = "name",
                type = "type",
                dimension = "dimension",
                characters = List(10) { characters ->
                    "character $characters"
                })
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `loading list without internet`() = runTest {
        val mockRepository = getMockkDetailLocationAndListWithoutInternet()
        val viewModel = LocationDetailViewModel(false, 1, mockRepository)
        viewModel.onRefreshedList()
        val actual = viewModel.charactersInDetailsFlow.first()
        val characters = List(5) {
            Character(
                id = it,
                name = "{$it name}",
                status = "{$it status}",
                species = "{$it species}",
                gender = "{$it gender}",
                image = "{$it image}",
                episode = List(5) { episode ->
                    episode.toString()
                },
                origin = CharacterLocationOrigin("$it name origin", "$it url"),
                location = CharacterLocationOrigin("$it name origin", "$it url")
            )
        }
        val expected = Lce.Content(characters)
        Assert.assertEquals(expected, actual)
    }

    private fun getMockkDetailLocationAndListWithInternet() =
        mockk<RickAndMortyRepositoryImpl> {
            val location = Location(
                id = 1,
                name = "name",
                type = "type",
                dimension = "dimension",
                characters = List(10) { characters ->
                    "character $characters"
                }
            )
            coEvery { this@mockk.getParticularLocation(any()) } coAnswers {
                Result.success(location)
            }
            val characters = List(5) {
                Character(
                    id = it,
                    name = "{$it name}",
                    status = "{$it status}",
                    species = "{$it species}",
                    gender = "{$it gender}",
                    image = "{$it image}",
                    episode = List(5) { episode ->
                        episode.toString()
                    },
                    origin = CharacterLocationOrigin("$it name origin", "$it url"),
                    location = CharacterLocationOrigin("$it name origin", "$it url")
                )
            }
            coEvery { this@mockk.getPlentyCharacter(any()) } coAnswers {
                Result.success(characters)
            }
        }

    private fun getMockkDetailLocationAndListWithoutInternet() =
        mockk<RickAndMortyRepositoryImpl> {
            val location = Location(
                id = 1,
                name = "name",
                type = "type",
                dimension = "dimension",
                characters = List(10) { characters ->
                    "character $characters"
                }
            )
            coEvery { this@mockk.getParticularLocationFromDatabase(any()) } coAnswers {
                Result.success(location)
            }
            val characters = List(5) {
                Character(
                    id = it,
                    name = "{$it name}",
                    status = "{$it status}",
                    species = "{$it species}",
                    gender = "{$it gender}",
                    image = "{$it image}",
                    episode = List(5) { episode ->
                        episode.toString()
                    },
                    origin = CharacterLocationOrigin("$it name origin", "$it url"),
                    location = CharacterLocationOrigin("$it name origin", "$it url")
                )
            }
            coEvery { this@mockk.getPlentyCharactersFromDatabase(any()) } coAnswers {
                Result.success(characters)
            }
        }
}
