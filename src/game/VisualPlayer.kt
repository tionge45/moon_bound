package game

import korlibs.korge.view.*
import korlibs.image.bitmap.*
import korlibs.math.geom.*
import interfaces.Collidable
import korlibs.event.*
import korlibs.io.file.std.*
import korlibs.time.*
import models.*
import kotlin.math.*

/**
 * The player’s visual representation and logic for movement and collisions.
 */
class VisualPlayer(
    private val model: Player,
    private val uiLayer: UILayer,
    texture: Bitmap
) : Container(), Collidable {

    private val body = image(texture) {
        anchor(.5, .5)
        scale(0.3)
    }.addTo(this)


    private val moveSpeed = 200.0 // pixels per second
    private var moveDirection = Point.ZERO
    private var hitCooldown = 0.0 // to prevent instant multiple hits

    init {
        this.x = model.x
        this.y = model.y

        addUpdater { dt ->
            updateMovement(dt.seconds)
            if (hitCooldown > 0) {
                hitCooldown -= dt.seconds
                if (hitCooldown <= 0) {
                    body.alpha = 1.0 // restore alpha when done blinking
                }
            }
        }
        println("VisualPlayer init - model.x=${model.x}, model.y=${model.y}, texture: ${texture.width}x${texture.height}")

    }

    private fun updateMovement(dt: Double) {
        val newX = (x + moveDirection.x * moveSpeed * dt)
            .coerceIn(0.0, stage?.views?.virtualWidth?.toDouble() ?: 800.0)
        val newY = (y + moveDirection.y * moveSpeed * dt)
            .coerceIn(0.0, stage?.views?.virtualHeight?.toDouble() ?: 600.0)

        model.x = newX
        model.y = newY

        x = newX
        y = newY
    }

    fun handleInput() {
        val input = stage?.views?.input ?: return

        var dx = 0.0
        var dy = 0.0

        if (input.keys.pressing(Key.LEFT) || input.keys.pressing(Key.A)) dx -= 1.0
        if (input.keys.pressing(Key.RIGHT) || input.keys.pressing(Key.D)) dx += 1.0
        if (input.keys.pressing(Key.UP) || input.keys.pressing(Key.W)) dy -= 1.0
        if (input.keys.pressing(Key.DOWN) || input.keys.pressing(Key.S)) dy+= 1.0

        val len = sqrt(dx * dx + dy * dy)
        moveDirection = if (len > 0.0) Point(dx / len, dy / len) else Point.ZERO
    }


    override fun getBounds(): Rectangle = body.getGlobalBounds()

    //on collision with obstacle
    //checks lives left and updates ui
    //adds cooldown to prevent multiple hits
    //blinks player to indicate hit(?)
    override fun onCollision(other: Collidable) {
        // Only take damage if not in cooldown
        if (hitCooldown <= 0) {
            model.lives = (model.lives - 1).coerceAtLeast(0)
            uiLayer.updateLives(model.lives)
            body.alpha = 0.4
            hitCooldown = 1.0 // 1 second invulnerability after hit
        }
    }
}




