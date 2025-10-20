package nl.t64.cot.screens.alchemist

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.WindowSelector


private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f
private const val ALCHEMIST_WINDOW_POSITION_X = 63f
private const val ALCHEMIST_WINDOW_POSITION_Y = 50f
private const val BREW_WINDOW_POSITION_X = 750f
private const val BREW_WINDOW_POSITION_Y = 50f
private const val RESOURCES_WINDOW_POSITION_X = 1519f
private const val RESOURCES_WINDOW_POSITION_Y = 394f

private const val TITLE_HEROES = "   Heroes"
private const val TITLE_ALCHEMIST = "   Alchemist"
private const val TITLE_BREW = "   Brew"
private const val TITLE_RESOURCES = "   Resources"

internal class AlchemistUI(
    stage: Stage,
    heroId: String,

    private val resourcesTable: ResourcesTable = ResourcesTable(),
    private val resourcesWindow: Window = Utils.createDefaultWindow(TITLE_RESOURCES, resourcesTable.container),

    private val alchemistTooltip: AlchemistTooltip = AlchemistTooltip(),
    private val brewTable: BrewTable = BrewTable(heroId, alchemistTooltip),
    private val brewWindow: Window = Utils.createDefaultWindow(TITLE_BREW, brewTable.container),

    alchemistTable: AlchemistTable = AlchemistTable(heroId),
    private val alchemistWindow: Window = Utils.createDefaultWindow(TITLE_ALCHEMIST, alchemistTable.table),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = Utils.createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf<WindowSelector>(brewTable),
    selectedTableIndex: Int = 0

) : ScreenUI(stage, heroesTable, tableList, selectedTableIndex) {

    init {
        super.init()
    }

    fun doAction() {
        getSelectedTable().doAction()
    }

    fun update() {
        resourcesTable.update()
        brewTable.update()
        heroesTable.update()

        resourcesWindow.pack()
        brewWindow.pack()
        alchemistWindow.pack()
        heroesWindow.pack()
    }

    fun stopTablesScrolling() {
        brewTable.stopScrolling()
    }

    override fun setWindowPositions() {
        resourcesWindow.setPosition(RESOURCES_WINDOW_POSITION_X, RESOURCES_WINDOW_POSITION_Y)
        brewWindow.setPosition(BREW_WINDOW_POSITION_X, BREW_WINDOW_POSITION_Y)
        alchemistWindow.setPosition(ALCHEMIST_WINDOW_POSITION_X, ALCHEMIST_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        alchemistTooltip.addToStage(stage)
        stage.addActor(resourcesWindow)
        stage.addActor(brewWindow)
        stage.addActor(alchemistWindow)
        stage.addActor(heroesWindow)
    }

}
