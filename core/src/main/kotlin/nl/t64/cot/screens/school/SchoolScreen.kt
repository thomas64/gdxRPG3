package nl.t64.cot.screens.school

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.academy.AcademyButtonLabels
import nl.t64.cot.screens.academy.AcademyScreenListener
import nl.t64.cot.screens.inventory.InventoryUtils


class SchoolScreen : ParchmentScreen() {

    private lateinit var schoolUI: SchoolUI
    private lateinit var npcId: String
    private lateinit var schoolId: String

    companion object {
        fun load(npcId: String, schoolId: String) {
            playSe(AudioEvent.SE_SCROLL)
            val schoolScreen = screenManager.getScreen(ScreenType.SCHOOL) as SchoolScreen
            schoolScreen.npcId = npcId
            schoolScreen.schoolId = schoolId
            screenManager.openParchmentLoadScreen(ScreenType.SCHOOL)
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
        stage.addListener(AcademyScreenListener({ closeScreen() },
                                                { upgradeSpell() },
                                                { selectPreviousHero() },
                                                { selectNextHero() },
                                                { selectPreviousTable() },
                                                { selectNextTable() },
                                                { toggleTooltip() }))

        schoolUI = SchoolUI(stage, npcId, schoolId)
        AcademyButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        stage.draw()
        schoolUI.update()
    }

    override fun hide() {
        schoolUI.unloadAssets()
        stage.clear()
    }

    override fun dispose() {
        stage.dispose()
    }

    private fun closeScreen() {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        playSe(AudioEvent.SE_SCROLL)
        fadeParchment()
    }

    private fun upgradeSpell() {
        schoolUI.upgradeSpell()
    }

    private fun selectPreviousHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        schoolUI.updateSelectedHero { InventoryUtils.selectPreviousHero() }
    }

    private fun selectNextHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        schoolUI.updateSelectedHero { InventoryUtils.selectNextHero() }
    }

    private fun selectPreviousTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        schoolUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        schoolUI.selectNextTable()
    }

    private fun toggleTooltip() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        schoolUI.toggleTooltip()
    }

}
