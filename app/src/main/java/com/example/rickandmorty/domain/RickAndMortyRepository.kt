package com.example.rickandmorty.domain

interface RickAndMortyRepository {

    suspend fun getAllCharactersFromDatabase(): Result<List<Character>>

    suspend fun getPlentyCharactersFromDatabase(list: List<Int>): Result<List<Character>>

    suspend fun getParticularCharacterFromDatabase(id: Int): Result<Character>

    suspend fun insertCharactersInDatabase(list: List<Character>): Result<Unit>

    suspend fun getParticularCharacter(id: Int): Result<Character>

    suspend fun getFilteredCharacters(
        status: String,
        species: String,
        gender: String,
        name: String,
        page: Int
    ): Result<List<Character>>

    suspend fun getPlentyCharacter(ids: String): Result<List<Character>>

    suspend fun getAllEpisodesFromDatabase(): Result<List<Episode>>

    suspend fun getPlentyEpisodesFromDatabase(list: List<Int>): Result<List<Episode>>

    suspend fun getParticularEpisodeFromDatabase(id: Int): Result<Episode>

    suspend fun insertEpisodesInDatabase(list: List<Episode>): Result<Unit>

    suspend fun getFilteredEpisode(code: String, name: String, page: Int): Result<List<Episode>>

    suspend fun getParticularEpisode(id: Int): Result<Episode>

    suspend fun getPlentyEpisodes(ids: String): Result<List<Episode>>

    suspend fun getAllLocationsFromDatabase(): Result<List<Location>>

    suspend fun getAllLocationsFromDatabase(list: List<Int>): Result<List<Location>>

    suspend fun getParticularLocationFromDatabase(id: Int): Result<Location>

    suspend fun insertLocationsInDatabase(list: List<Location>): Result<Unit>

    suspend fun getFilteredLocation(
        type: String,
        dimension: String,
        name: String,
        page: Int
    ): Result<List<Location>>

    suspend fun getParticularLocation(id: Int): Result<Location>

}
