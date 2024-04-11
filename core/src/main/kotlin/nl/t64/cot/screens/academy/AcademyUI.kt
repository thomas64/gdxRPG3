package nl.t64.cot.screens.academy

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.SkillsTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f
private const val TRAINER_WINDOW_POSITION_X = 63f
private const val TRAINER_WINDOW_POSITION_Y = 50f
private const val ACADEMY_WINDOW_POSITION_X = 630f
private const val ACADEMY_WINDOW_POSITION_Y = 50f
private const val SKILLS_WINDOW_POSITION_X = 985f
private const val SKILLS_WINDOW_POSITION_Y = 50f
private const val CALCS_WINDOW_POSITION_X = 1616f
private const val CALCS_WINDOW_POSITION_Y = 662f

private const val TITLE_HEROES = "   Heroes"
private const val TITLE_TRAINER = "   Trainer"
private const val TITLE_ACADEMY = "   Skills to train"
private const val TITLE_SKILLS = "   Trained Skills"
private const val TITLE_CALCS = "   Your Stats"

internal class AcademyUI(
    stage: Stage,
    npcId: String,
    academyId: String,

    private val personalityTooltip: PersonalityTooltip = PersonalityTooltip(),
    private val academyTooltip: AcademyTooltip = AcademyTooltip(),

    private val calcsTable: CalcsTable = CalcsTable(personalityTooltip),
    private val calcsWindow: Window = Utils.createDefaultWindow(TITLE_CALCS, calcsTable.container),

    private val skillsTable: SkillsTable = SkillsTable(personalityTooltip),
    private val skillsWindow: Window = Utils.createDefaultWindow(TITLE_SKILLS, skillsTable.container),

    private val academyTable: AcademyTable = AcademyTable(academyId, academyTooltip),
    private val academyWindow: Window = Utils.createDefaultWindow(TITLE_ACADEMY, academyTable.container),

    trainerTable: TrainerTable = TrainerTable(npcId),
    private val trainerWindow: Window = Utils.createDefaultWindow(TITLE_TRAINER, trainerTable.table),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = Utils.createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf<WindowSelector>(academyTable, skillsTable),
    selectedTableIndex: Int = 0

) : ScreenUI(stage, heroesTable, tableList, selectedTableIndex) {

    init {
        super.init()
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

    override fun setWindowPositions() {
        calcsWindow.setPosition(CALCS_WINDOW_POSITION_X, CALCS_WINDOW_POSITION_Y)
        skillsWindow.setPosition(SKILLS_WINDOW_POSITION_X, SKILLS_WINDOW_POSITION_Y)
        academyWindow.setPosition(ACADEMY_WINDOW_POSITION_X, ACADEMY_WINDOW_POSITION_Y)
        trainerWindow.setPosition(TRAINER_WINDOW_POSITION_X, TRAINER_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        personalityTooltip.addToStage(stage)
        academyTooltip.addToStage(stage)
        stage.addActor(calcsWindow)
        stage.addActor(skillsWindow)
        stage.addActor(academyWindow)
        stage.addActor(trainerWindow)
        stage.addActor(heroesWindow)
    }

}
