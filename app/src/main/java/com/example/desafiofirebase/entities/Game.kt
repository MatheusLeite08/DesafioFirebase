package com.example.desafiofirebase.entities

data class Game(
    val gameId: String = "",
    var gameYearCreation: String = "",
    var gameName: String = "",
    var gameImage: String = "",
    var gameDescription: String = ""
)