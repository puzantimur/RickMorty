package com.example.rickandmorty.data.db.converters

import androidx.room.TypeConverter
import com.example.rickandmorty.data.db.model.CharacterLocationOriginEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CharacterConverter {

    @TypeConverter
    fun fromRecipeList(value: CharacterLocationOriginEntity): String {
        val gson = Gson()
        val type = object : TypeToken<CharacterLocationOriginEntity>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toRecipeList(value: String): CharacterLocationOriginEntity {
        val gson = Gson()
        val type = object : TypeToken<CharacterLocationOriginEntity>() {}.type
        return gson.fromJson(value, type)
    }
}
