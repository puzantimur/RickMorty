package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.api.RickAndMortyApi
import com.example.rickandmorty.data.db.dao.CharacterDao
import com.example.rickandmorty.data.db.dao.EpisodeDao
import com.example.rickandmorty.data.db.dao.LocationDao
import com.example.rickandmorty.data.mapper.*
import com.example.rickandmorty.domain.Character
import com.example.rickandmorty.domain.Episode
import com.example.rickandmorty.domain.Location
import com.example.rickandmorty.domain.RickAndMortyRepository
import javax.inject.Inject

class RickAndMortyRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
    private val characterDao: CharacterDao,
    private val episodeDao: EpisodeDao,
    private val locationDao: LocationDao
) : RickAndMortyRepository {

    override suspend fun getAllCharactersFromDatabase(): Result<List<Character>> {
        return runCatching { characterDao.getAllCharactersFromDatabase().toDomainCharacters() }
    }

    override suspend fun getPlentyCharactersFromDatabase(list: List<Int>): Result<List<Character>> {
        return runCatching {
            characterDao.getPlentyCharactersFromDatabase(list).toDomainCharacters()
        }
    }

    override suspend fun getParticularCharacterFromDatabase(id: Int): Result<Character> {
        return runCatching {
            characterDao.getParticularCharacterFromDatabaseById(id).fromEntityToDomain()
        }
    }

    override suspend fun insertCharactersInDatabase(list: List<Character>): Result<Unit> {
        return runCatching { characterDao.insertCharactersInDatabase(list.toEntityCharacters()) }
    }

    override suspend fun getParticularCharacter(id: Int): Result<Character> {
        return runCatching { api.getParticularCharacter(id).toDomain() }
    }

    override suspend fun getFilteredCharacters(
        status: String,
        species: String,
        gender: String,
        name: String,
        page: Int,
    ): Result<List<Character>> {
        return runCatching {
            api.getFilteredCharacters(status, species, gender, name, page)
                .results.toDomainCharacterResult()
        }
    }

    override suspend fun getPlentyCharacter(ids: String): Result<List<Character>> {
        return runCatching { api.getMultipleCharacter(ids).toDomainCharacterResult() }
    }

    override suspend fun getAllEpisodesFromDatabase(): Result<List<Episode>> {
        return runCatching { episodeDao.getAllEpisodesFromDatabase().toDomainEpisode() }
    }

    override suspend fun getPlentyEpisodesFromDatabase(list: List<Int>): Result<List<Episode>> {
        return runCatching { episodeDao.getPlentyEpisodesFromDatabase(list).toDomainEpisode() }
    }

    override suspend fun getParticularEpisodeFromDatabase(id: Int): Result<Episode> {
        return runCatching { episodeDao.getParticularEpisodeFromDatabase(id).fromEntityToDomain() }
    }

    override suspend fun insertEpisodesInDatabase(list: List<Episode>): Result<Unit> {
        return runCatching { episodeDao.insertEpisodesInDatabase(list.toEntityEpisode()) }
    }

    override suspend fun getFilteredEpisode(
        code: String,
        name: String,
        page: Int
    ): Result<List<Episode>> {
        return runCatching {
            api.getFilteredEpisode(
                code,
                name,
                page
            ).results.toDomainEpisodeResult()
        }
    }

    override suspend fun getParticularEpisode(id: Int): Result<Episode> {
        return runCatching { api.getParticularEpisode(id).toDomain() }
    }

    override suspend fun getPlentyEpisodes(ids: String): Result<List<Episode>> {
        return runCatching { api.getMultipleEpisode(ids).toDomainEpisodeResult() }
    }

    override suspend fun getAllLocationsFromDatabase(): Result<List<Location>> {
        return runCatching { locationDao.getAllLocationsFromDatabase().toDomainLocation() }
    }

    override suspend fun getAllLocationsFromDatabase(list: List<Int>): Result<List<Location>> {
        return runCatching { locationDao.getPlentyLocationsFromDatabase(list).toDomainLocation() }
    }

    override suspend fun getParticularLocationFromDatabase(id: Int): Result<Location> {
        return runCatching {
            locationDao.getParticularLocationFromDatabase(id).fromEntityToDomain()
        }
    }

    override suspend fun insertLocationsInDatabase(list: List<Location>): Result<Unit> {
        return runCatching { locationDao.insertLocationInDatabase(list.toEntityLocation()) }
    }

    override suspend fun getFilteredLocation(
        type: String,
        dimension: String,
        name: String,
        page: Int
    ): Result<List<Location>> {
        return runCatching {
            api.getFilteredLocation(
                type,
                dimension,
                name,
                page
            ).results.toDomainLocationResult()
        }
    }

    override suspend fun getParticularLocation(id: Int): Result<Location> {
        return runCatching { api.getParticularLocation(id).toDomain() }
    }
}
