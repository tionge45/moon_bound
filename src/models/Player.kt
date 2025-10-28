package models

data class Player(
    var x: Double,
    var y: Double,
    var lives: Int = 3,
    var level: LevelType = LevelType.EASY, //DEFAULT,
    var timeLeft : Double = 90.0, //DEFAULT,
    var score: Int = 0
)
