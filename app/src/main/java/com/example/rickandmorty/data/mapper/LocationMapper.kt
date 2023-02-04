package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.api.model.LocationDTO
import com.example.rickandmorty.data.db.model.LocationEntity
import com.example.rickandmorty.domain.Location

fun List<LocationDTO>.toDomainLocationResult(): List<Location> = map { it.toDomain() }

fun LocationDTO.toDomain(): Location {
    return Location(
        id = id,
        name = name,
        type = type,
        dimension = dimension,
        characters = characters
    )
}

fun List<Location>.toEntityLocation(): List<LocationEntity> = map { it.toEntity() }

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        characters = characters,
        type = type,
        dimension = dimension
    )
}

fun List<LocationEntity>.toDomainLocation(): List<Location> =
    map { it.fromEntityToDomain() }

fun LocationEntity.fromEntityToDomain(): Location {
    return Location(
        id = id,
        name = name,
        characters = characters,
        type = type,
        dimension = dimension
    )
}
