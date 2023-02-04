package com.example.rickandmorty.presentation

import android.app.Application
import android.content.Context
import com.example.rickandmorty.data.di.AppComponent
import com.example.rickandmorty.data.di.DaggerAppComponent


class RickAndMortyApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is RickAndMortyApp -> appComponent
        else -> this.applicationContext.appComponent
    }
