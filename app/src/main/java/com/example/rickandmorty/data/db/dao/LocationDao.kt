package com.example.rickandmorty.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmorty.data.db.model.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationInDatabase(list: List<LocationEntity>)

    @Query("SELECT * FROM locationentity")
    suspend fun getAllLocationsFromDatabase(): List<LocationEntity>

    @Query("SELECT * FROM locationentity where id IN (:idList)")
    suspend fun getPlentyLocationsFromDatabase(idList: List<Int>): List<LocationEntity>

    @Query("SELECT * FROM locationentity where id LIKE :id ")
    suspend fun getParticularLocationFromDatabase(id: Int): LocationEntity

}
