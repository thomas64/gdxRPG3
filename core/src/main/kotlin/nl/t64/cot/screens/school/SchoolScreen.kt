package nl.t64.cot.screens.school

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
    private lateinit var listener: AcademyScreenListener

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
        setInputProcessors(stage)
        createAndSetListener()
        stage.addListener(listener)

        schoolUI = SchoolUI(stage, npcId, schoolId)
        AcademyButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        renderStage(dt)
        schoolUI.update()
    }

    override fun removeTriggersListener() {
        listener.removeTriggers()
    }

    private fun createAndSetListener() {
        listener = AcademyScreenListener(stage,
                                         { closeScreen() },
                                         { upgradeSpell() },
                                         { selectPreviousHero() },
                                         { selectNextHero() },
                                         { selectPreviousTable() },
                                         { selectNextTable() },
                                         { toggleTooltip() })
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
