package nl.t64.cot.screens.loot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen


abstract class LootScreen : ParchmentScreen() {

    lateinit var loot: Loot
    lateinit var lootTitle: String

    override fun show() {
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)

        val lootUI = LootUI(this, loot, lootTitle)
        lootUI.show(stage)
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        stage.draw()
    }

    override fun hide() {
        stage.clear()
    }

    override fun dispose() {
        stage.dispose()
    }

    open fun resolveLootAndCloseScreen(isAllTheLootCleared: Boolean) {
        if (isAllTheLootCleared) {
            resolveAfterClearingContent()
        }
        closeScreen()
    }

    fun closeScreen(fadeToScreen: ScreenType = ScreenType.WORLD) {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_NEXT)
        fadeParchment(fadeToScreen)
    }

    abstract fun resolveAfterClearingContent()

}
