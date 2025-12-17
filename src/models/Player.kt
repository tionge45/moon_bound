package models

import game.GameSettings

data class Player(
    var x: Double,
    var y: Double,
    var lives: Int = 3,
    var level: LevelType = GameSettings.selectedLevel, //DEFAULT,
    var timeLeft : Double = 90.0, //DEFAULT,
    var score: Int = 0,
    var session: GameSession? = null
)
