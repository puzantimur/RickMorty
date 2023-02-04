package com.example.rickandmorty.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rickandmorty.data.db.converters.CharacterConverter
import com.example.rickandmorty.data.db.converters.Converter
import com.example.rickandmorty.data.db.dao.CharacterDao
import com.example.rickandmorty.data.db.dao.EpisodeDao
import com.example.rickandmorty.data.db.dao.LocationDao
import com.example.rickandmorty.data.db.model.CharacterEntity
import com.example.rickandmorty.data.db.model.EpisodeEntity
import com.example.rickandmorty.data.db.model.LocationEntity

@Database(
    entities = [
        CharacterEntity::class,
        LocationEntity::class,
        EpisodeEntity::class
    ],
    version = 1
)
@TypeConverters(
    Converter::class,
    CharacterConverter::class,
)
abstract class AppDatabase : RoomDatabase() {

    abstract val characterDao: CharacterDao

    abstract val locationDao: LocationDao

    abstract val episodeDao: EpisodeDao
}
