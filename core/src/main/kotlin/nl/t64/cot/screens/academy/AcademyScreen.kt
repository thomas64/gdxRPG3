package nl.t64.cot.screens.academy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.inventory.InventoryUtils


class AcademyScreen : ParchmentScreen() {

    private lateinit var academyUI: AcademyUI
    private lateinit var npcId: String
    private lateinit var academyId: String

    companion object {
        fun load(npcId: String, academyId: String) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_SCROLL)
            val academyScreen = screenManager.getScreen(ScreenType.ACADEMY) as AcademyScreen
            academyScreen.npcId = npcId
            academyScreen.academyId = academyId
            screenManager.openParchmentLoadScreen(ScreenType.ACADEMY)
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
        stage.addListener(AcademyScreenListener({ closeScreen() },
                                                { upgradeSkill() },
                                                { selectPreviousHero() },
                                                { selectNextHero() },
                                                { selectPreviousTable() },
                                                { selectNextTable() },
                                                { toggleTooltip() }))

        academyUI = AcademyUI(stage, npcId, academyId)
        AcademyButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        stage.draw()
        academyUI.update()
    }

    override fun hide() {
        academyUI.unloadAssets()
        stage.clear()
    }

    override fun dispose() {
        stage.dispose()
    }

    private fun closeScreen() {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_SCROLL)
        AcademyUtils.trainerSkill = null
        AcademyUtils.heroSkill = null
        fadeParchment()
    }

    private fun upgradeSkill() {
        academyUI.upgradeSkill()
    }

    private fun selectPreviousHero() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        academyUI.updateSelectedHero { InventoryUtils.selectPreviousHero() }
    }

    private fun selectNextHero() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        academyUI.updateSelectedHero { InventoryUtils.selectNextHero() }
    }

    private fun selectPreviousTable() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        academyUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        academyUI.selectNextTable()
    }

    private fun toggleTooltip() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CONFIRM)
        academyUI.toggleTooltip()
    }

}