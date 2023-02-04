package com.example.rickandmorty.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmorty.data.db.model.CharacterEntity

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characterentity")
    suspend fun getAllCharactersFromDatabase(): List<CharacterEntity>

    @Query("SELECT * FROM characterentity where id LIKE :id ")
    suspend fun getParticularCharacterFromDatabaseById(id: Int): CharacterEntity

    @Query("SELECT * FROM characterentity where id IN (:idList)")
    suspend fun getPlentyCharactersFromDatabase(idList: List<Int>): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharactersInDatabase(recipes: List<CharacterEntity>)

}
