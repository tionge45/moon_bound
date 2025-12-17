package game
import korlibs.image.color.Colors
import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*

//lives, timer, moon progress bar, win overlay
class UILayer(parent: Container) {
    private val livesText: Text = parent.text("Lives: 3", textSize = 20.0, color = Colors.WHITE).addTo(parent).apply { x = 12.0; y = 10.0 }
    private val timerText: Text = parent.text("Time: 0", textSize = 20.0, color = Colors.WHITE).addTo(parent).apply { x = 640.0; y = 10.0 }
    private val score: Text = parent.text("", textSize = 36.0, color = Colors.YELLOW).addTo(parent).apply { x = 200.0; y = 250.0; visible = false }

    fun updateLives(lives: Int) {
        livesText.text = "Lives: $lives"
    }

    fun updateTimer(timeLeft: Double) {
        val t = timeLeft.toInt()
        timerText.text = "Time: $t"
    }

    private val moonBarBg = parent.solidRect(200.0, 10.0, Colors.DARKGRAY).apply {
        x = 300.0
        y = 10.0
    }

    private val moonBarFill = parent.solidRect(0.0, 10.0, Colors.BLUE).apply {
        x = 300.0
        y = 10.0
    }

    private val winOverlay = Container().addTo(parent).apply {
        visible = false
    }

    fun showWinOverlay(
        onExit: () -> Unit,
        onMenu: () -> Unit,
        onRestart: () -> Unit
    ) {
        winOverlay.visible = true
        winOverlay.removeChildren()

        winOverlay.solidRect(800.0, 600.0, Colors.BLACK.withAd(0.6))

        winOverlay.text(
            "CONGRATULATIONS, YOU WIN!",
            textSize = 36.0,
            color = Colors.WHITE
        ).addTo(winOverlay).centerXOnStage().apply { y = 180.0 }

        val restart = winOverlay.uiButton(size = Size(240, 36)) {
            text = "RESTART GAME"
            onClick { onRestart() }
        }.addTo(winOverlay)

        val menu = winOverlay.uiButton(size = Size(240, 36)) {
            text = "MAIN MENU"
            onClick { onMenu() }
        }.addTo(winOverlay)

        val exit = winOverlay.uiButton(size = Size(240, 36)) {
            text = "EXIT"
            onClick { onExit() }
        }.addTo(winOverlay)

        restart.centerXOnStage().apply { y = 260.0 }
        menu.centerXOnStage().apply { y = 310.0 }
        exit.centerXOnStage().apply { y = 360.0 }
    }

    fun updateMoonProgress(progress: Double) {
        moonBarFill.width = 200.0 * progress.coerceIn(0.0, 1.0)
    }

    fun showWinMessage(finalScore: Int) {
        val finalText = finalScore.toInt()
        score.text= "You Win! Score: $finalText"
    }
}









