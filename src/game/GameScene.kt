package game

import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import korlibs.time.*
import models.GameSession
import models.LevelType
import models.Player
import models.GameState

/**
 * GameScene: composes the background, engine and UI layers and runs the update loop.
 *
 * This file uses existing models package (Player, GameSession, LevelType, GameState).
 */
class GameScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        // Create top-level containers (background, game, ui)
        val backgroundLayer = BackgroundLayer(this)
        val gameLayerContainer = Container().addTo(this)
        val uiLayer = UILayer(this)

        //  initial models
        val playerModel = Player(
            x = 400.0, y = 520.0,
            lives = 3,
            level = LevelType.EASY,
            timeLeft = LevelType.EASY.duration,
            score = 0
        )

        val session = GameSession(
            player = playerModel,
            obstacle = mutableListOf(),
            currentLevelType = LevelType.EASY,
            state = GameState.MENU
        )

        // Engine creation
        val engine = GameEngine(gameLayerContainer, session, uiLayer, backgroundLayer, resourcesVfs["img.png"].readBitmap())

        //Game starts. State = RUNNING. Engine updates.
        session.state = GameState.RUNNING


        // Main update loop
        addUpdater { dt ->
            engine.update(dt.seconds)
        }

    }
}
