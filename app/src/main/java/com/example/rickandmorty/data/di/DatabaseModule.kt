package com.example.rickandmorty.data.di

import android.content.Context
import androidx.room.Room
import com.example.rickandmorty.data.db.AppDatabase
import com.example.rickandmorty.data.db.dao.CharacterDao
import com.example.rickandmorty.data.db.dao.EpisodeDao
import com.example.rickandmorty.data.db.dao.LocationDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "RickAndMortDB"
        ).build()
    }

    @Singleton
    @Provides
    fun providesCharacterDao(db: AppDatabase): CharacterDao = db.characterDao

    @Singleton
    @Provides
    fun providesLocationDao(db: AppDatabase): LocationDao = db.locationDao

    @Singleton
    @Provides
    fun providesEpisodeDao(db: AppDatabase): EpisodeDao = db.episodeDao

}
