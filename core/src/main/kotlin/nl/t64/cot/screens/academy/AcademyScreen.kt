package nl.t64.cot.screens.academy

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.inventory.InventoryUtils


class AcademyScreen : ParchmentScreen() {

    private lateinit var academyUI: AcademyUI
    private lateinit var npcId: String
    private lateinit var academyId: String

    companion object {
        fun load(npcId: String, academyId: String) {
            playSe(AudioEvent.SE_SCROLL)
            val academyScreen = screenManager.getScreen(ScreenType.ACADEMY) as AcademyScreen
            academyScreen.npcId = npcId
            academyScreen.academyId = academyId
            screenManager.openParchmentLoadScreen(ScreenType.ACADEMY)
        }
    }

    override fun show() {
        setInputProcessors(stage)
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
        renderStage(dt)
        academyUI.update()
    }

    private fun upgradeSkill() {
        academyUI.upgradeSkill()
    }

    private fun selectPreviousHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        academyUI.updateSelectedHero { InventoryUtils.selectPreviousHero() }
    }

    private fun selectNextHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        academyUI.updateSelectedHero { InventoryUtils.selectNextHero() }
    }

    private fun selectPreviousTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        academyUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        academyUI.selectNextTable()
    }

    private fun toggleTooltip() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        academyUI.toggleTooltip()
    }

}
