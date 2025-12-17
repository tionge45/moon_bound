package game

import korlibs.image.color.Colors
import korlibs.math.geom.Rectangle
import interfaces.Updatable
import interfaces.Collidable
import korlibs.image.bitmap.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.view.*
import models.*


/**
 * VisualObstacle: visual wrapper for the Obstacle model
 */
class VisualObstacle(
    parent: Container,
    val model: Obstacle,
    asteroidBmp: Bitmap
) : Updatable, Collidable {


    private val view = Image(asteroidBmp).addTo(parent).apply {
        anchor(0.5, 0.5)
        this.scaledWidth = model.size
        this.scaledHeight = model.size
        println("OBSTACLE SCALE = ${model.size / asteroidBmp.width}")
        println("BMP WIDTH = ${asteroidBmp.width}, MODEL SIZE = ${model.size}")
        x = model.x
        y = model.y
    }


    override fun update(time: Double) {
        // move according to model speed (model.speed is px per second)
        model.y += model.speed * time
        view.y = model.y
        view.x = model.x
    }

    override fun getBounds(): Rectangle {
        val shrink = model.size * 0.15
        return Rectangle(
            view.x + shrink,
            view.y + shrink,
            model.size - shrink * 2,
            model.size - shrink * 2
        )
    }


    override fun onCollision(other: Collidable){
        if (other is VisualPlayer) {
            other.onCollision(this)
            destroy()
        }
    }

    fun destroy() {
        view.removeFromParent()
    }


}
