
package game

import korlibs.image.bitmap.Bitmap
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs

/**
 * AvatarLoader manages the loading of the avatars from the resources folder and passes them to
 * the GameScene where they are loaded and used for selection
 */
object AvatarLoader {
    // List of avatar filenames
    private val avatarFileNames = listOf("Sprite.png", "Alien.png", "SPACESHIP.png")

    // Cache loaded bitmaps to avoid reloading
    private val avatarCache = mutableMapOf<String, Bitmap>()

    suspend fun loadSelectedAvatar(): Bitmap {
        val selectedIndex = GameSettings.selectedAvatar

        // Validate index(3 Avatars)
        val index = if (selectedIndex in 0..2) selectedIndex else 0

        return loadAvatarByIndex(index)
    }

    suspend fun loadAvatarByIndex(index: Int): Bitmap {
        // Validate and clamp index
        val safeIndex = index.coerceIn(0, avatarFileNames.size - 1)
        val fileName = avatarFileNames[safeIndex]

        // Check cache first
        return avatarCache.getOrPut(fileName) {
            try {
                resourcesVfs[fileName].readBitmap()
            } catch (e: Exception) {
                println("ERROR: Could not load avatar $fileName, using default")
                // Fallback to default avatar
                resourcesVfs["Sprite.png"].readBitmap()
            }
        }
    }


    fun clearCache() {
        avatarCache.clear()
    }
}
