package com.example.rickandmorty.data.di

import android.content.Context
import com.example.rickandmorty.presentation.MainActivity
import com.example.rickandmorty.presentation.ui.character.CharactersFragment
import com.example.rickandmorty.presentation.ui.character.detail.CharacterDetailFragment
import com.example.rickandmorty.presentation.ui.episode.EpisodeFragment
import com.example.rickandmorty.presentation.ui.episode.detail.EpisodeDetailFragment
import com.example.rickandmorty.presentation.ui.location.LocationFragment
import com.example.rickandmorty.presentation.ui.location.detail.LocationDetailFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(fragment: CharactersFragment)
    fun inject(fragment: CharacterDetailFragment)
    fun inject(fragment: EpisodeFragment)
    fun inject(fragment: LocationFragment)
    fun inject(fragment: EpisodeDetailFragment)
    fun inject(fragment: LocationDetailFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}
