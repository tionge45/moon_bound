package game

import korlibs.korge.view.Container
import korlibs.korge.view.SolidRect
import korlibs.korge.view.addTo
import korlibs.korge.view.circle
import korlibs.image.color.Colors
import korlibs.time.*
import kotlin.random.Random
import kotlin.time.*

/**
 * BackgroundLayer: simple gradient + stars that slowly move to create depth
 */
class BackgroundLayer(private val parent: Container) {
    private val width = 800.0
    private val height = 600.0

    private val bg: SolidRect = SolidRect(width, height, Colors["#0b1220"]).addTo(parent)
    private val stars = mutableListOf<korlibs.korge.view.View>()

    init {
        // Create a scattered starfield
        repeat(70) {
            val s = parent.circle(radius = Random.nextDouble(0.6, 1.8), fill = Colors["#FFFFFF"].withAd(Random.nextDouble(0.25, 0.9))).addTo(parent)
            s.x = Random.nextDouble(0.0, width)
            s.y = Random.nextDouble(0.0, height)
            stars.add(s)
        }
    }

    fun update(dtSeconds: Duration) {
        // Scroll stars downward slowly
        for (s in stars) {
            s.y += 10.0 * dtSeconds.seconds // 10 px per second base
            if (s.y > height) s.y = 0.0
        }
    }
}
