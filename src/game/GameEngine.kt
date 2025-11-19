package game

import interfaces.Updatable
import korlibs.image.bitmap.*
import korlibs.korge.view.*
import models.*
import kotlin.math.*
import kotlin.random.Random
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
    private val playerTexture: Bitmap
) : Updatable {

    private val visualObstacles = mutableListOf<VisualObstacle>()
    private val visualPlayer: VisualPlayer = VisualPlayer(
            session.player,
            uiLayer,
            texture = playerTexture,
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
            val vo = VisualObstacle(gameContainer, model)
            visualObstacles.add(vo)
        }

        gameContainer.addChild(visualPlayer)
        println("Added visualPlayer to gameContainer; visualPlayer pos=(${visualPlayer.x},${visualPlayer.y})")

    }



    override fun update(time: Double) {
        if (session.state != GameState.RUNNING) return

        val dt = time.coerceAtMost(0.05) // clamp in case of big frame lags
        // update background (gentle star scroll)
        backgroundLayer.update(dt.seconds)

        // Update player view (handles input & bounding)
        // Update player view (handles input & bounding)
        visualPlayer.handleInput()


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

        // Collision checks: naive O(n) check against player
        val playerBounds = visualPlayer.getBounds()
        val collided = visualObstacles.firstOrNull { vo ->
            vo.getBounds().intersects(playerBounds)
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

    private fun trySpawnObstacle() {
        // generate obstacle model with random width/size and speed influenced by level
        val size = Random.nextDouble(24.0, 48.0)
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
        val vo = VisualObstacle(gameContainer, obstacleModel)
        visualObstacles.add(vo)
        println("added VisualObstacle view at ${vo.getBounds()}")
    }

    private fun minHorizontalGap(): Double = minHorizontalGap

    private companion object {
        const val minHorizontalGap = 48.0
    }

}
