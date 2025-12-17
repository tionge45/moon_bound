package game

import interfaces.Updatable
import korlibs.image.bitmap.Bitmap
import korlibs.korge.view.*
import korlibs.math.geom.*
import models.*
import kotlin.math.*

class VisualMoon(
    parent: Container,
    private val session: GameSession,
    moonTexture: Bitmap
) : Updatable {

    private val moonView = Image(moonTexture).addTo(parent).apply {
        anchor(0.5, 0.5)
        alpha = 0.0
        scale = START_SCALE
        position(session.moonPosition)
        smoothing = true
    }

    // timing
    private var visibleTimer = 0.0

    override fun update(dt: Double) {
        if (session.state == GameState.WIN) return
        if (session.state != GameState.RUNNING) return

        handleVisibility(dt)
        if (session.isMoonVisible) {
            updateApproach(dt)
            applyVisuals()
            checkLanding()
        }
    }

    private fun handleVisibility(dt: Double) {
        visibleTimer += dt

        if (!session.isMoonVisible && visibleTimer >= APPEAR_DELAY) {
            session.isMoonVisible = true
        }

        if (session.isMoonVisible) {
            moonView.alpha = min(1.0, moonView.alpha + dt / FADE_IN_DURATION)
        }
    }

    private fun updateApproach(dt: Double) {
        val playerPos = Point(session.player.x, session.player.y)
        val distance = playerPos.distanceTo(session.moonPosition)

        val shouldApproach =
            playerPos.y < APPROACH_Y_THRESHOLD &&
                distance < APPROACH_DISTANCE

        val delta = dt / APPROACH_DURATION

        session.moonApproachProgress = when {
            shouldApproach ->
                (session.moonApproachProgress + delta).coerceAtMost(1.0)

            distance > RETREAT_DISTANCE ->
                (session.moonApproachProgress - delta * 0.5).coerceAtLeast(0.0)

            else -> session.moonApproachProgress
        }
    }


    private fun applyVisuals() {
        val t = easeOut(session.moonApproachProgress)
        moonView.scale = lerp(START_SCALE, TARGET_SCALE, t)
    }


    private fun checkLanding() {
        if (session.moonApproachProgress >= 1.0) {
            session.state = GameState.WIN
        }
    }

    fun getLandingPoint(): Point =
        Point(moonView.x, moonView.y + moonView.scaledHeight * 0.25)


    fun getBounds(): Rectangle =
        Rectangle.fromBounds(
            moonView.x - moonView.scaledWidth / 2,
            moonView.y - moonView.scaledHeight / 2,
            moonView.x + moonView.scaledWidth / 2,
            moonView.y + moonView.scaledHeight / 2
        )

    fun destroy() {
        moonView.removeFromParent()
    }

    private fun lerp(a: Double, b: Double, t: Double): Double =
        a + (b - a) * t

    private fun easeOut(t: Double): Double =
        1 - (1 - t) * (1 - t)

    companion object {
        private const val APPEAR_DELAY = 30.0
        private const val FADE_IN_DURATION = 2.0

        private const val APPROACH_DURATION = 5.0
        private const val APPROACH_DISTANCE = 300.0
        private const val RETREAT_DISTANCE = 350.0
        private const val APPROACH_Y_THRESHOLD = 200.0

        private const val START_SCALE = 0.5
        private const val TARGET_SCALE = 2.0
    }
}
