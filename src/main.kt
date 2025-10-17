import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.image.color.*
import korlibs.math.geom.*
import korlibs.event.*
import korlibs.korge.view.align.*


suspend fun main() = Korge(
    windowSize = Size(800, 600),
    backgroundColor = Colors["#0d0d0d"], // deep space color
    title = "Moon Bound"
) {
    sceneContainer().changeTo { GameScene() }
}

class GameScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        // Create player (jetpack)
        val player = solidRect(width = 40.0, height = 60.0, color = Colors["#00ffcc"]) {
            position(380, 500) // start near bottom
        }

        fun View.collidesWith(other: View): Boolean {
            val a = this.globalBounds
            val b = other.globalBounds
            return a.intersects(b)
        }

        // Movement speed
        val speed = 5.0

        // Keyboard controls
        keys {
            down(Key.LEFT) { player.x -= speed }
            down(Key.RIGHT) { player.x += speed }
            down(Key.UP) { player.y -= speed }
            down(Key.DOWN) { player.y += speed }
        }

        // Simple moon target
        val moon = circle(radius = 30.0, fill = Colors["#e0e0e0"]) {
            position(380, 50)
        }

        // Basic win condition check
        addUpdater {
            if (player.collidesWith(moon)) {
                text("YOU REACHED THE MOON!", 32.0, Colors.WHITE) {
                    centerXOnStage()
                    centerYOnStage()
                }
                player.removeFromParent()
            }
        }
    }
}
