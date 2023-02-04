package com.example.rickandmorty.presentation.extensions

import java.util.regex.Pattern

private const val pattern = "[0-9]+"

fun String.findId(): String {
    var id = ""
    val matcher = Pattern.compile(pattern).matcher(this)
    if (matcher.find()) {
        id = matcher.group() as String
    }
    return id
}