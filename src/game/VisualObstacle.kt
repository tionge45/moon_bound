package game

import korlibs.image.color.Colors
import korlibs.math.geom.Rectangle
import interfaces.Updatable
import interfaces.Collidable
import korlibs.korge.view.*
import models.*


/**
 * VisualObstacle: visual wrapper for the Obstacle model
 */
class VisualObstacle(
    parent: Container,
    val model: Obstacle
) : Updatable, Collidable {

    // create a rounded rectangle sized to model.size
    private val view = parent.solidRect( //temporally using solidRect instead of roundRect
        model.size, model.size, Colors["#b57530"],
    ).addTo(parent)

    init {
        view.x = model.x
        view.y = model.y


    }

    override fun update(time: Double) {
        // move according to model speed (model.speed is px per second)
        model.y += model.speed * time
        view.y = model.y
        view.x = model.x
    }

    override fun getBounds(): Rectangle = Rectangle(view.x, view.y, model.size, model.size)

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
