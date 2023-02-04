package com.example.rickandmorty.data.di

import dagger.Module

@Module(includes = [NetworkModule::class, AppBindModule::class, DatabaseModule::class])
class AppModule