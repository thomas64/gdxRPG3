package nl.t64.cot.screens.academy

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.SkillsTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.tooltip.AcademyTooltip
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f
private const val TRAINER_WINDOW_POSITION_X = 63f
private const val TRAINER_WINDOW_POSITION_Y = 50f
private const val ACADEMY_WINDOW_POSITION_X = 630f
private const val ACADEMY_WINDOW_POSITION_Y = 50f
private const val SKILLS_WINDOW_POSITION_X = 985f
private const val SKILLS_WINDOW_POSITION_Y = 50f
private const val CALCS_WINDOW_POSITION_X = 1501f
private const val CALCS_WINDOW_POSITION_Y = 480f

private const val TITLE_HEROES = "   Heroes"
private const val TITLE_TRAINER = "   Trainer"
private const val TITLE_ACADEMY = "   Skills to train"
private const val TITLE_SKILLS = "   Trained Skills"
private const val TITLE_CALCS = "   Stats"

internal class AcademyUI(
    private val stage: Stage,
    npcId: String,
    academyId: String
) {

    private val personalityTooltip = PersonalityTooltip()
    private val academyTooltip = AcademyTooltip()
    private val calcsTable = CalcsTable(personalityTooltip)
    private val calcsWindow = Utils.createDefaultWindow(TITLE_CALCS, calcsTable.container)
    private val skillsTable = SkillsTable(personalityTooltip)
    private val skillsWindow = Utils.createDefaultWindow(TITLE_SKILLS, skillsTable.container)
    private val academyTable = AcademyTable(academyId, academyTooltip)
    private val academyWindow = Utils.createDefaultWindow(TITLE_ACADEMY, academyTable.container)

    private val trainerWindow = Utils.createDefaultWindow(TITLE_TRAINER, TrainerTable(npcId).table)
    private val heroesTable = HeroesTable()
    private val heroesWindow = Utils.createDefaultWindow(TITLE_HEROES, heroesTable.heroes)

    private val tableList = listOf<WindowSelector>(academyTable, skillsTable)
    private var selectedTableIndex = 0

    init {
        setWindowPositions()
        addToStage()
        setFocusOnSelectedTable()
        stage.addAction(Actions.sequence(
            Actions.delay(0.1f),
            Actions.run { getSelectedTable().selectCurrentSlot() }
        ))
    }

    fun update() {
        calcsTable.update()
        skillsTable.update()
        academyTable.update()

        heroesTable.update()

        calcsWindow.pack()
        skillsWindow.pack()
        academyWindow.pack()
        trainerWindow.pack()
        heroesWindow.pack()
    }

    fun upgradeSkill() {
        if (getSelectedTable() is AcademyTable) {
            academyTable.upgradeSkill()
        }
    }

    fun updateSelectedHero(updateHero: () -> Unit) {
        getSelectedTable().deselectCurrentSlot()
        updateHero.invoke()
        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    fun selectPreviousTable() {
        getSelectedTable().hideTooltip()
        getSelectedTable().deselectCurrentSlot()
        selectedTableIndex--
        if (selectedTableIndex < 0) {
            selectedTableIndex = tableList.size - 1
        }
        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    fun selectNextTable() {
        getSelectedTable().hideTooltip()
        getSelectedTable().deselectCurrentSlot()
        selectedTableIndex++
        if (selectedTableIndex >= tableList.size) {
            selectedTableIndex = 0
        }
        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    fun toggleTooltip() {
        val currentSlot = getSelectedTable().getCurrentSlot()
        val currentTooltip = getSelectedTable().getCurrentTooltip()
        currentTooltip.toggle(currentSlot)
    }

    fun unloadAssets() {
        heroesTable.disposePixmapTextures()
    }

    private fun setWindowPositions() {
        calcsWindow.setPosition(CALCS_WINDOW_POSITION_X, CALCS_WINDOW_POSITION_Y)
        skillsWindow.setPosition(SKILLS_WINDOW_POSITION_X, SKILLS_WINDOW_POSITION_Y)
        academyWindow.setPosition(ACADEMY_WINDOW_POSITION_X, ACADEMY_WINDOW_POSITION_Y)
        trainerWindow.setPosition(TRAINER_WINDOW_POSITION_X, TRAINER_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    private fun addToStage() {
        personalityTooltip.addToStage(stage)
        academyTooltip.addToStage(stage)
        stage.addActor(calcsWindow)
        stage.addActor(skillsWindow)
        stage.addActor(academyWindow)
        stage.addActor(trainerWindow)
        stage.addActor(heroesWindow)
    }

    private fun setFocusOnSelectedTable() {
        getSelectedTable().setKeyboardFocus(stage)
        stage.draw()
    }

    private fun getSelectedTable(): WindowSelector {
        return tableList[selectedTableIndex]
    }

}
