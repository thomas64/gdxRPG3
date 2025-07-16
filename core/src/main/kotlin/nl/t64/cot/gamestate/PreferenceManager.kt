package nl.t64.cot.gamestate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Preferences
import ktx.preferences.get
import ktx.preferences.set
import nl.t64.cot.constants.Constant


private const val SETTINGS_FILE = "settings.dat"
private const val SETTING_SCREEN = "isFullscreen"
private const val SETTING_MUSIC = "isMusicOn"
private const val SETTING_SOUND = "isSoundOn"
private const val SETTING_TUTORIAL = "isTutorialOn"
private const val SETTING_DEBUG = "isDebugModeOn"
private const val FULLSCREEN_DEFAULT = true
private const val MUSIC_DEFAULT = true
private const val SOUND_DEFAULT = true
private const val TUTORIAL_DEFAULT = true
private const val DEBUG_MODE_DEFAULT = false

class PreferenceManager {

    private val preferences: Preferences = Gdx.app.getPreferences(SETTINGS_FILE)
    var isFullscreen = preferences[SETTING_SCREEN, FULLSCREEN_DEFAULT]
    var isMusicOn = preferences[SETTING_MUSIC, MUSIC_DEFAULT]
    var isSoundOn = preferences[SETTING_SOUND, SOUND_DEFAULT]
    var isTutorialOn = preferences[SETTING_TUTORIAL, TUTORIAL_DEFAULT]
    var isDebugModeOn = DEBUG_MODE_DEFAULT

    fun toggleFullscreen() {
        if (Gdx.graphics.isFullscreen) {
            setWindowedMode()
        } else {
            setFullscreenMode()
        }
        isFullscreen = Gdx.graphics.isFullscreen
        preferences[SETTING_SCREEN] = isFullscreen
        preferences.flush()
    }

    fun setFullscreenAccordingToPreference() {
        if (isFullscreen) setFullscreenMode()
    }

    fun toggleMusic() {
        isMusicOn = isMusicOn.not()
        preferences[SETTING_MUSIC] = isMusicOn
        preferences.flush()
    }

    fun toggleSound() {
        isSoundOn = isSoundOn.not()
        preferences[SETTING_SOUND] = isSoundOn
        preferences.flush()
    }

    fun toggleTutorial() {
        isTutorialOn = isTutorialOn.not()
        preferences[SETTING_TUTORIAL] = isTutorialOn
        preferences.flush()
    }

    fun toggleDebugMode() {
        isDebugModeOn = isDebugModeOn.not()
    }

    private fun setWindowedMode() {
        Gdx.graphics.setWindowedMode(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT)
    }

    private fun setFullscreenMode() {
        val displayMode = Gdx.graphics.displayModes
            .filter { hasGivenResolution(it) }
            .maxByOrNull { it.refreshRate }
            ?: throw NoSuchElementException()
        Gdx.graphics.setFullscreenMode(displayMode)
    }

    private fun hasGivenResolution(mode: Graphics.DisplayMode): Boolean {
        return (mode.width == Constant.SCREEN_WIDTH
                && mode.height == Constant.SCREEN_HEIGHT)
    }

}
