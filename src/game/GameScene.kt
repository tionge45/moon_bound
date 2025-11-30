package game

import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import korlibs.korge.view.align.*
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

        val asteroidBitmap = resourcesVfs["asteroid_obstacle.png"].readBitmap()


        val gameOverContainer = Container().addTo(this).apply {
            visible = false
        }

        text("GAME OVER", textSize = 48.0)
            .addTo(gameOverContainer)
            .centerXOn(gameOverContainer)
            .apply { y = 200.0 }

        val restartButton = text("TRY AGAIN?", textSize = 32.0)
            .addTo(gameOverContainer)
            .centerXOn(gameOverContainer)
            .apply { y = 300.0 }

        restartButton.onClick {
            suspend {
                sceneContainer.changeTo{ GameScene() }

            }

        }


        //  initial models
        val playerModel = Player(
            x = 400.0, y = 520.0,
            lives = 3,
            level = LevelType.EASY,
            timeLeft = LevelType.EASY.duration,
            score = 0,

        )

        val session = GameSession(
            player = playerModel,
            obstacle = mutableListOf(),
            currentLevelType = LevelType.EASY,
            state = GameState.MENU
        )

        //inject session into player model
        playerModel.session = session



        // Engine creation
        val engine = GameEngine(gameLayerContainer, session, uiLayer, backgroundLayer, asteroidBitmap, resourcesVfs["img.png"].readBitmap())

        //Game starts. State = RUNNING. Engine updates.
        session.state = GameState.RUNNING


        // Main update loop
        addUpdater { dt ->
            engine.update(dt.seconds)

            if(session.state == GameState.GAME_OVER){
                gameOverContainer.visible = true
            }
        }

    }
}
