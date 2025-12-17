package game

import interfaces.Updatable
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.io.async.*
import korlibs.korge.tween.*
import korlibs.korge.view.*
import korlibs.logger.*
import korlibs.math.geom.*
import korlibs.math.geom.Circle
import korlibs.math.interpolation.*
import korlibs.time.*
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import models.*
import scene.*
import kotlin.math.*
import kotlin.random.Random
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

/**
 * GameEngine coordinates models and their visual wrappers.
 *
 * Responsibilities:
 * - Spawn obstacles periodically (based on level)
 * - Update all updatables (player view, obstacles)
 * - Maintain spacing by spawn cooldown
 * - Clean up off-screen obstacles
 * - Integrate with GameSession model state
 */
class GameEngine(
    private val gameContainer: Container,
    private val session: GameSession,
    private val uiLayer: UILayer,
    private val backgroundLayer: BackgroundLayer,
    private val obstacleTexture: Bitmap,
    private val playerBitmap:Bitmap
    private val moonTexture: Bitmap,
    private val onWin: () -> Unit
) : Updatable {

    private val visualMoon: VisualMoon = VisualMoon(gameContainer, session, moonTexture)
    private val visualObstacles = mutableListOf<VisualObstacle>()
    private val visualPlayer: VisualPlayer = VisualPlayer(
            session.player,
            uiLayer,
            texture = playerTexture,
            session = session
        )


    // spawn control (seconds)
    private var spawnCooldown = 0.0
    private val baseSpawnIntervalSeconds = 0.9 // approx spawn every 0.9s at EASY
    private val minHorizontalGap = 48.0 // min horizontal gap in px to avoid stacking

    // simple screen boundaries (mirrors Main.kt windowSize)
    private val screenWidth = 800.0
    private val screenHeight = 600.0


    init {
        // If session already has obstacles (unlikely on first run), create wrappers
        session.obstacle.forEach { model ->
            val vo = VisualObstacle(gameContainer, model, asteroidBmp = obstacleTexture)
            visualObstacles.add(vo)
        }

        gameContainer.addChild(visualPlayer)
        println("Added visualPlayer to gameContainer; visualPlayer pos=(${visualPlayer.x},${visualPlayer.y})")

    }



    override fun update(time: Double) {
        if (session.state != GameState.RUNNING) return

        val dt = time.coerceAtMost(0.05)
        // Update moon (appearance & approach)
        visualMoon.update(dt)

        // update background (gentle star scroll)
        backgroundLayer.update(dt.seconds)

        // Update player view (handles input & bounding)
        visualPlayer.handleInput()

        checkMoonLanding()

        uiLayer.updateMoonProgress(session.moonApproachProgress)


        // Update obstacles
        val iter = visualObstacles.iterator()
        while (iter.hasNext()) {
            val vo = iter.next()
            vo.update(dt)

            // If an obstacle went off screen - remove it
            if (vo.model.y - vo.model.size > screenHeight + 20) {
                vo.destroy()
                session.obstacle.remove(vo.model)
                iter.remove()
            }
        }


        val collided = visualObstacles.firstOrNull { obstacle ->
            val playerBounds = visualPlayer.getBounds()
            val obstacleBounds = obstacle.getBounds()

            val playerCenter = Point(playerBounds.centerX, playerBounds.centerY)
            val obstacleCenter = Point(obstacleBounds.centerX, obstacleBounds.centerY)

            val playerRadius = min(playerBounds.width, playerBounds.height) / 2.0
            val obstacleRadius = min(obstacleBounds.width, obstacleBounds.height) / 2.0

            val distance = playerCenter.distanceTo(obstacleCenter)
            distance < (playerRadius + obstacleRadius)
        }

        if (collided != null) {
            // process collision
            visualPlayer.onCollision(other = collided)
            // remove collided obstacle
            collided.destroy()
            session.obstacle.remove(collided.model)
            visualObstacles.remove(collided)
            uiLayer.updateLives(session.player.lives)
            if (session.player.lives <= 0) {
                session.state = GameState.GAME_OVER
            }
        }

        // Spawning logic: interval adjusted by level multiplier
        val spawnInterval = baseSpawnIntervalSeconds / max(0.5, session.currentLevelType.speedMultiplier)
        spawnCooldown += dt
        if (spawnCooldown >= spawnInterval) {
            spawnCooldown = 0.0
            trySpawnObstacle()
        }

        // update UI timer
        session.player.timeLeft = (session.player.timeLeft - dt).coerceAtLeast(0.0)
        uiLayer.updateTimer(session.player.timeLeft)
        if (session.player.timeLeft <= 0.0) {
            session.state = GameState.GAME_OVER
        }
    }

    private var winSequenceStarted = false

    private fun checkMoonLanding() {
        if (!winSequenceStarted &&
            session.isMoonVisible &&
            session.moonApproachProgress >= 1.0
        ) {
            winSequenceStarted = true

            session.state = GameState.WIN
            session.player.score += 5000

            onWin()
        }
    }

    fun snapPlayerToMoon() {
        val landing = visualMoon.getLandingPoint()

        visualPlayer.x = landing.x
        visualPlayer.y = landing.y
    }


    fun createWinParticles() {
        repeat(300) { i ->
            val color = when (i % 3) {
                0 -> Colors.GOLD
                1 -> Colors.CYAN
                else -> Colors.PINK
            }

            val particle = gameContainer.circle(
                radius = 3.0,
                fill = color
            )

            val startX = visualPlayer.x + Random.nextDouble(-25.0, 25.0)
            val startY = visualPlayer.y + Random.nextDouble(-25.0, 25.0)

            particle.position(startX, startY)
            particle.alpha = 1.0
            particle.scale = 1.0

            val delay = i * 0.1
            val duration = 2.0

            var elapsed = 0.0
            var started = false

            particle.addUpdater { dt->
                elapsed += dt.seconds

                // Staggered start
                if (!started) {
                    if (elapsed < delay) return@addUpdater
                    started = true
                    elapsed = 0.0
                }

                val t = (elapsed / duration).coerceIn(0.0, 1.0)

                // EASE_OUT approximation
                val eased = 1 - (1 - t) * (1 - t)

                particle.alpha = 1.0 - eased
                particle.scale = 1.0 - 0.9 * eased
                particle.y = startY - 100.0 * eased

                if (t >= 1.0) {
                    particle.removeFromParent()
                }
            }
        }
    }


    private fun trySpawnObstacle() {
        // generate obstacle model with random width/size and speed influenced by level
        val size = Random.nextDouble(24.0, 60.0)
        val speedBase = 100.0 // px per second base
        val speed = speedBase * session.currentLevelType.speedMultiplier * Random.nextDouble(0.8, 1.3)

        // choose x so it doesn't overlap existing near-top obstacles
        val tries = 6
        var xCandidate: Double
        var accepted = false
        var attempt = 0
        do {
            xCandidate = Random.nextDouble(0.0, screenWidth - size)
            // check distance to recent obstacles near top
            val conflict = visualObstacles.any { vo ->
                val dy = vo.model.y
                // only consider obstacles that are near top for spacing decision
                if (dy < 120.0) {
                    val dx = abs(vo.model.x - xCandidate)
                    dx < (vo.model.size + size) / 2 + minHorizontalGap()
                } else false
            }
            if (!conflict) {
                accepted = true
            }
            attempt++
        } while (!accepted && attempt < tries)

        if (!accepted) {
            // fallback: allow spawn anyway
        }

        val obstacleModel = Obstacle(
            x = xCandidate,
            y = -size - 8.0,
            speed = speed,
            size = size
        )
        println("spawn obstacle: x=$xCandidate y=${-size} size=$size speed=$speed")
        session.obstacle.add(obstacleModel)
        val vo = VisualObstacle(gameContainer, obstacleModel, obstacleTexture)
        visualObstacles.add(vo)
        println("added VisualObstacle view at ${vo.getBounds()}")
    }

    private fun minHorizontalGap(): Double = minHorizontalGap

    private companion object {
        const val minHorizontalGap = 48.0
    }


}
