package scene


import game.GameSettings
import korlibs.image.color.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.input.onClick
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.io.async.launchImmediately
import models.LevelType

/**
 * OptionsScene is the implementation of the Options button on the MenuScene.
 * It contains the Level choosing and avatar choosing
*/
class OptionsScene : Scene() {
    companion object {
        var selectedAvatar = 0
        val avatarDisplayNames = listOf( "Sprite", "Alien", "Space Ship")

        var selectedLevel = GameSettings.selectedLevel
    }

    override suspend fun SContainer.sceneMain() {
        solidRect(views.virtualWidth, views.virtualHeight, Colors["#0d0d0d"])

        text("OPTIONS", 72.0, Colors.WHITE) {
            centerXOnStage()
            y = 40.0
        }

        // Tabs
        val avatarTab = uiButton(size = Size(220, 60)) {
            text = "AVATAR"; textSize = 30.0
            position(views.virtualWidth / 2 - 240, 120.0)
        }
        val levelTab = uiButton(size = Size(220, 60)) {
            text = "LEVEL"; textSize = 30.0
            position(views.virtualWidth / 2 + 20, 120.0)
        }


        // Avatar section
        val avatarSection = container { y = 210.0; visible = true }
        lateinit var updateAvatarSelection: () -> Unit
        avatarSection.apply {
            text("CHOOSE YOUR SPACESHIP", 48.0, Colors.CYAN) {
                centerXOnStage();

            }



            //Avatar Buttons
            val spriteBtn = uiButton(size = Size(100, 20)) {
                text = "SPRITE"; textSize = 15.0
                centerXOnStage();
                y = 80.0
            }
            val alienBtn = uiButton(size = Size(100, 20)) {
                text = "ALIEN"; textSize = 15.0
                centerXOn(spriteBtn); alignTopToBottomOf(spriteBtn, 20.0)
            }
            val spaceshipBtn = uiButton(size = Size(100, 20)) {
                text = "SPACESHIP"; textSize = 15.0
                centerXOn(spriteBtn); alignTopToBottomOf(alienBtn, 20.0)
            }

            val currentAvatarText = text("SELECTED: ${avatarDisplayNames[selectedAvatar]}", 34.0, Colors.CYAN) {
                centerXOnStage(); y = 420.0
            }

            updateAvatarSelection = {
                spriteBtn.alphaF = if (selectedAvatar == 0) 1f else 0.7f
                alienBtn.alphaF = if (selectedAvatar == 1) 1f else 0.7f
                spaceshipBtn.alphaF = if (selectedAvatar == 2) 1f else 0.7f
                currentAvatarText.text = "SELECTED: ${avatarDisplayNames[selectedAvatar]}"
            }

            // Button click handlers

            spriteBtn.onClick {
                selectedAvatar = 0
                GameSettings.selectedAvatar = 0
                updateAvatarSelection()
            }
            alienBtn.onClick {
                selectedAvatar = 1
                GameSettings.selectedAvatar = 1
                updateAvatarSelection()
            }
            spaceshipBtn.onClick {
                selectedAvatar = 2
                GameSettings.selectedAvatar = 2
                updateAvatarSelection()
            }

            updateAvatarSelection()
        }



        // Level section
        lateinit var updateLevelSelection: () -> Unit
        val levelSection = container { y = 210.0; visible = false }
        levelSection.apply {
            text("CHOOSE LEVEL", 48.0, Colors.CYAN) { centerXOnStage() }

            val easyBtn = uiButton(size = Size(100, 20)) {
                text = "EASY"; textSize = 15.0
                centerXOnStage(); y = 80.0

            }
            val medBtn = uiButton(size = Size(100, 20)) {
                text = "MEDIUM"; textSize = 15.0
                centerXOn(easyBtn); alignTopToBottomOf(easyBtn, 25.0)
            }
            val hardBtn = uiButton(size = Size(100, 20)) {
                text = "HARD"; textSize = 28.0
                centerXOn(easyBtn); alignTopToBottomOf(medBtn, 25.0)

            }
            val infBtn = uiButton(size = Size(100, 20)) {
                text = "INFINITY GAME"; textSize = 15.0
                centerXOn(easyBtn); alignTopToBottomOf(hardBtn, 25.0)

            }

            val currentLevelText = text("CURRENT: ${selectedLevel.name.replace("_", " ")}", 34.0, Colors.CYAN) {
                centerXOnStage(); y = 560.0
            }

             updateLevelSelection = {
                easyBtn.alphaF = if (selectedLevel == LevelType.EASY) 1f else 0.7f
                medBtn.alphaF = if (selectedLevel == LevelType.MEDIUM) 1f else 0.7f
                hardBtn.alphaF = if (selectedLevel == LevelType.HARD) 1f else 0.7f
                infBtn.alphaF = if (selectedLevel == LevelType.INFINITY_GAME) 1f else 0.7f
                currentLevelText.text = "CURRENT: ${selectedLevel.name.replace("_", " ")}"
            }

            easyBtn.onClick { selectedLevel = LevelType.EASY
                GameSettings.selectedLevel = LevelType.EASY
                updateLevelSelection()
            }
            medBtn.onClick { selectedLevel = LevelType.MEDIUM
                GameSettings.selectedLevel = LevelType.MEDIUM
                updateLevelSelection()
            }
            hardBtn.onClick { selectedLevel = LevelType.HARD
                GameSettings.selectedLevel = LevelType.HARD
                updateLevelSelection()
            }
            infBtn.onClick { selectedLevel = LevelType.INFINITY_GAME
                GameSettings.selectedLevel = LevelType.INFINITY_GAME
                updateLevelSelection()
            }

            updateLevelSelection()
        }

        // Tab switching
        avatarTab.onClick {
            avatarSection.visible = true; levelSection.visible = false
            avatarTab.alphaF = 1f; levelTab.alphaF = 0.6f
        }
        levelTab.onClick {
            avatarSection.visible = false; levelSection.visible = true
            avatarTab.alphaF = 0.6f; levelTab.alphaF = 1f
        }

        // Initial tab state
        avatarTab.alphaF = 1f; levelTab.alphaF = 0.6f




        // BACK button
        uiButton(size = Size(300, 70)) {
            text = "BACK TO MENU"; textSize = 32.0
            centerXOnStage()
            alignBottomToBottomOf(this@sceneMain, 50.0)
            onClick { launchImmediately { sceneContainer.changeTo { MenuScene() } } }
        }
    }
}
