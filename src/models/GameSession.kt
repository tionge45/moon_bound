package models

import korlibs.math.geom.*

data class GameSession(
    var player: Player,
    var obstacle: MutableList<Obstacle>,
    var currentLevelType: LevelType,
    var state: GameState = GameState.MENU, //DEFAULT
    var moonPosition: Point = Point(400.0, 100.0),
    var moonScale: Double = 0.5,
    var moonApproachProgress: Double = 0.0,
    var isMoonVisible: Boolean = false,
    var moonAppearTime: Double = 0.0
)
