package models

enum class LevelType(val speedMultiplier: Double, val duration:Double) {
    EASY(1.0, 90.0),
    MEDIUM(1.5, 60.0),
    HARD(2.0, 30.0),
    INFINITY_GAME(2.0, Double.POSITIVE_INFINITY)
}
