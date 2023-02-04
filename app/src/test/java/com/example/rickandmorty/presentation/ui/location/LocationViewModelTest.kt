package com.example.rickandmorty.presentation.ui.location

import com.example.rickandmorty.data.repository.RickAndMortyRepositoryImpl
import com.example.rickandmorty.domain.Location
import com.example.rickandmorty.presentation.model.FilterLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LocationViewModelTest : ViewModelTest() {

    @Test
    fun `loading with internet and database is not empty`() = runTest {
        val mockRepository = getMockLocationWithInternetAndDatabaseIsNotEmpty()
        val viewModel = LocationViewModel(true, mockRepository)
        viewModel.onQueryChanged(FilterLocation())
        val actual = viewModel.dataFlow.first()
        val expected = List(10) {
            Location(
                id = it,
                name = "{$it name}",
                type = "{$it type}",
                dimension = "{$it dimension}",
                characters = List(10) { characters ->
                    characters.toString()
                }
            )
        }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `loading with internet and database is empty`() = runTest {
        val mockRepository = getMockLocationWithInternetAndDatabaseIsEmpty()
        val viewModel = LocationViewModel(true, mockRepository)
        viewModel.onQueryChanged(FilterLocation())
        val actual = viewModel.dataFlow.first()
        val expected = List(10) {
            Location(
                id = it,
                name = "{$it name}",
                type = "{$it type}",
                dimension = "{$it dimension}",
                characters = List(10) { characters ->
                    characters.toString()
                }
            )
        }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `loading without internet and database is not empty`() = runTest {
        val mockRepository = getMockkLocationFromFilledDatabase()
        val viewModel = LocationViewModel(false, mockRepository)
        viewModel.onQueryChanged(FilterLocation())
        val actual = viewModel.dataFlow.first()
        val expected = List(10) {
            Location(
                id = it,
                name = "{$it name}",
                type = "{$it type}",
                dimension = "{$it dimension}",
                characters = List(10) { characters ->
                    characters.toString()
                }
            )
        }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `loading without internet and database is empty`() = runTest {
        val mockRepository = getMockkLocationFromEmptyDatabase()
        val viewModel = LocationViewModel(false, mockRepository)
        viewModel.onQueryChanged(FilterLocation())
        val actual = viewModel.dataFlow.first()
        val expected = emptyList<Location>()
        Assert.assertEquals(expected, actual)
    }

    private fun getMockkLocationFromFilledDatabase() = mockk<RickAndMortyRepositoryImpl> {
        val locations = List(10) {
            Location(
                id = it,
                name = "{$it name}",
                type = "{$it type}",
                dimension = "{$it dimension}",
                characters = List(10) { characters ->
                    characters.toString()
                }
            )
        }
        coEvery { this@mockk.getFilteredLocation(any(), any(), any(), any()) } coAnswers {
            Result.failure(Exception("test error"))
        }
        coEvery { this@mockk.getAllLocationsFromDatabase() } coAnswers {
            Result.success(locations)
        }
    }

    private fun getMockkLocationFromEmptyDatabase() = mockk<RickAndMortyRepositoryImpl> {
        coEvery { this@mockk.getFilteredLocation(any(), any(), any(), any()) } coAnswers {
            Result.failure(Exception("test error"))
        }
        coEvery { this@mockk.getAllLocationsFromDatabase() } coAnswers {
            Result.success(emptyList())
        }
    }

    private fun getMockLocationWithInternetAndDatabaseIsNotEmpty(sendResult: Boolean = true) =
        mockk<RickAndMortyRepositoryImpl> {
            val locations = List(10) {
                Location(
                    id = it,
                    name = "{$it name}",
                    type = "{$it type}",
                    dimension = "{$it dimension}",
                    characters = List(10) { characters ->
                        characters.toString()
                    }
                )
            }
            coEvery { this@mockk.getFilteredLocation(any(), any(), any(), any()) } coAnswers {
                if (sendResult) {
                    Result.success(locations)
                } else {
                    Result.failure(Exception("test error"))
                }
            }
            coEvery { this@mockk.getAllLocationsFromDatabase() } coAnswers {
                if (sendResult) {
                    Result.success(locations)
                } else {
                    Result.failure(Exception("test error"))
                }
            }
            coEvery { this@mockk.insertLocationsInDatabase(locations) } coAnswers {
                if (sendResult) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("test error"))
                }
            }
        }

    private fun getMockLocationWithInternetAndDatabaseIsEmpty() =
        mockk<RickAndMortyRepositoryImpl> {
            val locations = List(10) {
                Location(
                    id = it,
                    name = "{$it name}",
                    type = "{$it type}",
                    dimension = "{$it dimension}",
                    characters = List(10) { characters ->
                        characters.toString()
                    }
                )
            }
            coEvery { this@mockk.getFilteredLocation(any(), any(), any(), any()) } coAnswers {
                Result.success(locations)
            }
            coEvery { this@mockk.getAllLocationsFromDatabase() } coAnswers {
                Result.failure(Exception("test error"))
            }
            coEvery { this@mockk.insertLocationsInDatabase(locations) } coAnswers {
                Result.success(Unit)
            }
        }
}
