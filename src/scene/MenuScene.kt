package scene

import korlibs.image.color.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.input.onClick
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import game.GameScene
import korlibs.io.async.launchImmediately
import kotlin.random.Random

class MenuScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        // Background
        solidRect(views.virtualWidth, views.virtualHeight, Colors["#0d0d0d"])

        // Title
        text("MOON BOUND", 80.0, Colors.WHITE) {
            centerXOnStage()
            alignTopToTopOf(this@sceneMain, padding = 120.0)
        }

        // Subtitle
        text("LUNAR ADVENTURE", 28.0, Colors["#bbbbbb"]) {
            centerXOnStage()
            alignTopToTopOf(this@sceneMain, padding = 210.0)
        }


        // START GAME
        val startBtn = uiButton(size = Size(200, 20)) {
            text = "START GAME"
            textSize = 34.0
            centerXOnStage()
            y = 320.0
            onClick {
                launchImmediately {
                    sceneContainer.changeTo{GameScene()}
                }
            }
        }

        // OPTIONS
        val optionsBtn = uiButton(size = Size(200, 20)) {
            text = "OPTIONS"
            textSize = 28.0
            centerXOn(startBtn)
            alignTopToBottomOf(startBtn, padding = 20.0)
            onClick {
                launchImmediately {
                    sceneContainer.changeTo{ OptionsScene() }
                }

            }
        }

        // EXIT
        val exitBtn = uiButton(size = Size(200, 20)) {
            text = "EXIT"
            textSize = 28.0
            centerXOn(startBtn)
            alignTopToBottomOf(optionsBtn, padding = 20.0)
            onClick { views.gameWindow.close() }
        }

        // Decorations
        addSpaceDecorations()

        // Footer
        text("Arrow Keys to Move ", 18.0, Colors["#888888"]) {
            centerXOnStage()
            alignBottomToBottomOf(this@sceneMain, padding = 40.0)
        }
    }

    private fun Container.addSpaceDecorations() {
        // Random stars
        repeat(35) {
            val size = Random.nextDouble(1.0, 4.0)
            solidRect(size, size, Colors.WHITE.withA((0.6 + Random.nextDouble(0.4)).toInt()))
                .xy(
                    Random.nextInt(50, views.virtualWidth.toInt() - 50),
                    Random.nextInt(50, views.virtualHeight.toInt() - 50)
                )
        }

        // Little Moons near the Title
        circle(12.0, Colors["#cccccc"]).position(105, 100)
        circle(8.0, Colors["#cccccc"]).position(140, 130)
        circle(10.0, Colors["#cccccc"]).position(130, 95)
    }
}
