package com.example.desafiofirebase.entities

import java.io.Serializable

data class Game(
    val gameId: String = "",
    var gameYearCreation: String = "",
    var gameName: String = "",
    var gameImage: String = "",
    var gameDescription: String = "",
    var imgId: String = ""
): Serializable