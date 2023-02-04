package com.example.rickandmorty.data.mapper

import com.example.rickandmorty.data.api.model.EpisodeDTO
import com.example.rickandmorty.data.db.model.EpisodeEntity
import com.example.rickandmorty.domain.Episode

fun List<EpisodeDTO>.toDomainEpisodeResult(): List<Episode> = map { it.toDomain() }

fun EpisodeDTO.toDomain(): Episode {
    return Episode(
        id = id,
        name = name,
        date = date,
        episode = episode,
        characters = characters
    )
}

fun List<Episode>.toEntityEpisode(): List<EpisodeEntity> = map { it.toEntity() }

fun Episode.toEntity(): EpisodeEntity {
    return EpisodeEntity(
        id = id,
        name = name,
        date = date,
        episode = episode,
        characters = characters
    )
}

fun List<EpisodeEntity>.toDomainEpisode(): List<Episode> = map { it.fromEntityToDomain() }

fun EpisodeEntity.fromEntityToDomain(): Episode {
    return Episode(
        id = id,
        name = name,
        date = date,
        episode = episode,
        characters = characters
    )
}
