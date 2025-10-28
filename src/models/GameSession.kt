package models

data class GameSession(
    var player: Player,
    var obstacle: MutableList<Obstacle>,
    var currentLevelType: LevelType,
    var state: GameState = GameState.MENU //DEFAULT
)
