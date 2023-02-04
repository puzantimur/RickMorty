package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.api.model.CharacterDTO
import com.example.rickandmorty.data.api.model.CharacterLocationOriginDTO
import com.example.rickandmorty.data.db.model.CharacterEntity
import com.example.rickandmorty.data.db.model.CharacterLocationOriginEntity
import com.example.rickandmorty.domain.Character
import com.example.rickandmorty.domain.CharacterLocationOrigin

fun List<CharacterDTO>.toDomainCharacterResult(): List<Character> = map { it.toDomain() }

fun CharacterDTO.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image,
        episode = episode,
        origin = origin.toDomain(),
        location = location.toDomain()
    )
}

fun List<Character>.toEntityCharacters(): List<CharacterEntity> = map { it.toEntity() }

fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image,
        episode = episode,
        origin = origin.toEntity(),
        location = location.toEntity()
    )
}

fun List<CharacterEntity>.toDomainCharacters(): List<Character> =
    map { it.fromEntityToDomain() }

fun CharacterEntity.fromEntityToDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image,
        episode = episode,
        origin = origin.toDomain(),
        location = location.toDomain()
    )
}

fun CharacterLocationOriginDTO.toDomain(): CharacterLocationOrigin {
    return CharacterLocationOrigin(
        name = name,
        url = url
    )
}

fun CharacterLocationOriginEntity.toDomain(): CharacterLocationOrigin {
    return CharacterLocationOrigin(
        name = name,
        url = url
    )
}

fun CharacterLocationOrigin.toEntity(): CharacterLocationOriginEntity {
    return CharacterLocationOriginEntity(
        name = name,
        url = url
    )
}
