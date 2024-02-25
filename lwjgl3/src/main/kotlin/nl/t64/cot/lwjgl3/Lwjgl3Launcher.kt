@file:JvmName("Lwjgl3Launcher")

package nl.t64.cot.lwjgl3

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import nl.t64.cot.CrystalOfTime
import nl.t64.cot.constants.Constant


/** Launches the desktop (LWJGL3) application. */
fun main() {

    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired()) {
        return
    }

    Lwjgl3Application(
        CrystalOfTime(),
        Lwjgl3ApplicationConfiguration().apply {
            setTitle(Constant.TITLE)
            useVsync(true)
            setWindowIcon("sprites/icon.png", *(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
            setResizable(false)
            setIdleFPS(60)
            setForegroundFPS(60)
            setWindowedMode(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT)
            setPreferencesConfig("T64.nl/", Files.FileType.External)
        }
    )

}
