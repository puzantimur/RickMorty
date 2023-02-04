package com.example.rickandmorty.data.api

import com.example.rickandmorty.data.api.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character/{id}")
    suspend fun getParticularCharacter(@Path("id") id: Int): CharacterDTO

    @GET("character")
    suspend fun getFilteredCharacters(
        @Query("status") status: String,
        @Query("species") species: String,
        @Query("gender") gender: String,
        @Query("name") name: String,
        @Query("page") page: Int
    ): CharactersRequestDTO

    @GET("character/{ids}")
    suspend fun getMultipleCharacter(@Path("ids") ids: String): List<CharacterDTO>

    @GET("episode")
    suspend fun getFilteredEpisode(
        @Query("episode") code: String,
        @Query("name") name: String,
        @Query("page") page: Int
    ): EpisodeRequestDTO

    @GET("episode/{id}")
    suspend fun getParticularEpisode(@Path("id") id: Int): EpisodeDTO

    @GET("episode/{ids}")
    suspend fun getMultipleEpisode(@Path("ids") ids: String): List<EpisodeDTO>

    @GET("location")
    suspend fun getFilteredLocation(
        @Query("type") type: String,
        @Query("dimension") dimension: String,
        @Query("name") name: String,
        @Query("page") page: Int
    ): LocationRequestDTO

    @GET("location/{id}")
    suspend fun getParticularLocation(@Path("id") id: Int): LocationDTO

}
