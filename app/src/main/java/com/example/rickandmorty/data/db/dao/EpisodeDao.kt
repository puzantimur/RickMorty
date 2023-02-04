package com.example.rickandmorty.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmorty.data.db.model.EpisodeEntity

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodesInDatabase(list: List<EpisodeEntity>)

    @Query("SELECT * FROM episodeentity")
    suspend fun getAllEpisodesFromDatabase(): List<EpisodeEntity>

    @Query("SELECT * FROM episodeentity where id IN (:idList)")
    suspend fun getPlentyEpisodesFromDatabase(idList: List<Int>): List<EpisodeEntity>

    @Query("SELECT * FROM episodeentity where id LIKE :id ")
    suspend fun getParticularEpisodeFromDatabase(id: Int): EpisodeEntity

}
