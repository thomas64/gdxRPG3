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
            //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
            //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
            useVsync(true)
            //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
            //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
//            setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1)
            //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
            //// useful for testing performance, but can also be very stressful to some hardware.
            //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

            setIdleFPS(60)
            setForegroundFPS(60)

            setWindowedMode(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT)
            setResizable(false)
            //// You can change these files; they are in lwjgl3/src/main/resources/ .
            //// They can also be loaded from the root of assets/ .
            setWindowIcon("sprites/icon.png", *(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
            setPreferencesConfig("T64.nl/", Files.FileType.External)
        }
    )

}
