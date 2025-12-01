import game.*
import korlibs.image.color.*
import korlibs.korge.*
import korlibs.korge.scene.*
import scene.*

suspend fun main() = Korge(
    windowWidth = 1280,
    windowHeight = 720,
    virtualWidth = 800,
    virtualHeight = 600,
    bgcolor = Colors["#0d0d0d"], // background
    title = "Moon Bound"
) {

    sceneContainer().changeTo{MenuScene()}
}
