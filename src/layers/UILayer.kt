package game

import korlibs.korge.view.Container
import korlibs.korge.view.Text
import korlibs.korge.view.addTo
import korlibs.image.color.Colors
import korlibs.korge.view.text

/**
 * UILayer: shows lives and timer. UI text is kept simple and readable.
 */
class UILayer(parent: Container) {
    private val livesText: Text = parent.text("Lives: 3", textSize = 20.0, color = Colors.WHITE).addTo(parent).apply { x = 12.0; y = 10.0 }
    private val timerText: Text = parent.text("Time: 0", textSize = 20.0, color = Colors.WHITE).addTo(parent).apply { x = 640.0; y = 10.0 }

    fun updateLives(lives: Int) {
        livesText.text = "Lives: $lives"
    }

    fun updateTimer(timeLeft: Double) {
        val t = timeLeft.toInt()
        timerText.text = "Time: $t"
    }
}
