import game.*
import korlibs.image.color.*
import korlibs.korge.*
import korlibs.korge.scene.*
import scene.*

suspend fun main() = Korge(
    windowWidth = 1280,
    windowHeight = 720,
    bgcolor = Colors["#0d0d0d"], // background
    title = "Moon Bound"
) {
    sceneContainer().changeTo { GameScene() }
}
