package nl.t64.cot.screens.school

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.SpellsTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f
private const val TEACHER_WINDOW_POSITION_X = 63f
private const val TEACHER_WINDOW_POSITION_Y = 50f
private const val SCHOOL_WINDOW_POSITION_X = 561f
private const val SCHOOL_WINDOW_POSITION_Y = 50f
private const val SPELLS_WINDOW_POSITION_X = 985f
private const val SPELLS_WINDOW_POSITION_Y = 50f
private const val CALCS_WINDOW_POSITION_X = 1536f
private const val CALCS_WINDOW_POSITION_Y = 610f

private const val TITLE_HEROES = "   Heroes"
private const val TITLE_TEACHER = "   Teacher"
private const val TITLE_SCHOOL = "   Spells to learn"
private const val TITLE_SPELLS = "   Known Spells"
private const val TITLE_CALCS = "   Stats"

internal class SchoolUI(
    stage: Stage,
    npcId: String,
    schoolId: String,

    private val personalityTooltip: PersonalityTooltip = PersonalityTooltip(),
    private val schoolTooltip: SchoolTooltip = SchoolTooltip(),

    private val calcsTable: CalcsTable = CalcsTable(personalityTooltip),
    private val calcsWindow: Window = Utils.createDefaultWindow(TITLE_CALCS, calcsTable.container),

    private val spellsTable: SpellsTable = SpellsTable(personalityTooltip),
    private val spellsWindow: Window = Utils.createDefaultWindow(TITLE_SPELLS, spellsTable.container),

    private val schoolTable: SchoolTable = SchoolTable(schoolId, schoolTooltip),
    private val schoolWindow: Window = Utils.createDefaultWindow(TITLE_SCHOOL, schoolTable.container),

    teacherTable: TeacherTable = TeacherTable(npcId),
    private val teacherWindow: Window = Utils.createDefaultWindow(TITLE_TEACHER, teacherTable.table),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = Utils.createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf<WindowSelector>(schoolTable, spellsTable),
    selectedTableIndex: Int = 0

) : ScreenUI(stage, heroesTable, tableList, selectedTableIndex) {

    init {
        super.init()
    }

    fun upgradeSpell() {
        if (getSelectedTable() is SchoolTable) {
            schoolTable.upgradeSpell()
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
        spellsTable.update()
        schoolTable.update()

        heroesTable.update()

        calcsWindow.pack()
        spellsWindow.pack()
        schoolWindow.pack()
        teacherWindow.pack()
        heroesWindow.pack()
    }

    override fun setWindowPositions() {
        calcsWindow.setPosition(CALCS_WINDOW_POSITION_X, CALCS_WINDOW_POSITION_Y)
        spellsWindow.setPosition(SPELLS_WINDOW_POSITION_X, SPELLS_WINDOW_POSITION_Y)
        schoolWindow.setPosition(SCHOOL_WINDOW_POSITION_X, SCHOOL_WINDOW_POSITION_Y)
        teacherWindow.setPosition(TEACHER_WINDOW_POSITION_X, TEACHER_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        personalityTooltip.addToStage(stage)
        schoolTooltip.addToStage(stage)
        stage.addActor(calcsWindow)
        stage.addActor(spellsWindow)
        stage.addActor(schoolWindow)
        stage.addActor(teacherWindow)
        stage.addActor(heroesWindow)
    }

}
