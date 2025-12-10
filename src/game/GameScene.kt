package game

import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.async.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.scene.Scene
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.*
import models.GameSession
import models.LevelType
import models.Player
import models.GameState
import scene.*

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

        text("GAME OVER!", textSize = 48.0)
            .addTo(gameOverContainer)
            .centerXOnStage()
            .apply { y = 200.0 }

        val restartBtn = uiButton(size = Size(200, 20)).addTo(gameOverContainer) {
            text = "RESTART"
            textSize = 34.0
            centerXOnStage()
            y = 320.0
            onClick {
                launchImmediately {
                    sceneContainer.changeTo{GameScene()}
                }
            }
        }

        val exitToMainMenuButton = uiButton(size = Size(250, 20)).addTo(gameOverContainer) {
            text = "EXIT TO MAIN"
            textSize = 34.0
            centerOn(restartBtn)
            alignTopToBottomOf(restartBtn, padding = 20.0)
            onClick {
                launchImmediately {
                    sceneContainer.changeTo{MenuScene()}
                }
            }
        }

//        val restartButton = uiButton("TRY AGAIN?", size = Size(220, 60))
//            .addTo(gameOverContainer)
//            .centerXOn(gameOverContainer)
//            .apply { y = 300.0 }

//        restartButton.onClick {
//            suspend {
//                sceneContainer.changeTo{ GameScene() }
//
//            }
//
//        }


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
