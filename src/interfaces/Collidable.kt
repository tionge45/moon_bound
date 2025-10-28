package interfaces

import korlibs.math.geom.*

interface Collidable {
    fun getBounds(): Rectangle
    fun onCollision(other: Collidable) //between 2 objects that implement collidable(e.g. player and obstacle)
}
