package scene

import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.korge.view.align.*
import korlibs.event.*
import korlibs.korge.input.*
import korlibs.math.geom.*

class MainScene : Scene() {

    override suspend fun SContainer.sceneMain() {
        sceneContainer.changeTo<MenuScene>()
        //game world boundaries
        val width = 1024.0
        val height = 768.0

        //background
        solidRect(width, height, Colors["#87CEEB"].withAd(0.5)) {
            centerXOnStage()
        }

        //player object
        val player = solidRect(width = 40.0, height = 60.0, color = Colors["#00ffcc"]) {
            position(500, 668)//should start near bottom
        }

        //moon
        val moon = circle(radius = 30.0, fill = Colors["#e0e0e0"]) {
            position(580, 100)
        }

        //speed
        val speed = 5.0

        // keyboard controls with boundary limits
        keys {
            down(Key.LEFT)  { player.x = (player.x - speed).coerceAtLeast(0.0) }
            down(Key.RIGHT) { player.x = (player.x + speed).coerceAtMost(width - player.width) }
            down(Key.UP)    { player.y = (player.y - speed).coerceAtLeast(0.0) }
            down(Key.DOWN)  { player.y = (player.y + speed).coerceAtMost(height - player.height) }
        }

        fun View.collidesWith(other: View): Boolean {
            val a = this.globalBounds
            val b = other.globalBounds
            return a.intersects(b)
        }

        //basic condition check
        addUpdater {
            if (player.collidesWith(moon)){
                text("YOU WON! YOU REACHED THE MOON!", 32.0, Colors.WHITE){
                    centerXOnStage()
                    centerYOnStage()
                }
                player.removeFromParent()
            }
        }
    }
}




