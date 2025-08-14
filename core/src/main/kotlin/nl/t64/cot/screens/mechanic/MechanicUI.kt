package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f
private const val MECHANIC_WINDOW_POSITION_X = 63f
private const val MECHANIC_WINDOW_POSITION_Y = 50f
private const val RESOURCES_WINDOW_POSITION_X = 1616f
private const val RESOURCES_WINDOW_POSITION_Y = 532f

private const val TITLE_HEROES = "   Heroes"
private const val TITLE_MECHANIC = "   Mechanic"
private const val TITLE_RESOURCES = "   Resources"

internal class MechanicUI(
    stage: Stage,
    heroId: String,

    private val personalityTooltip: PersonalityTooltip = PersonalityTooltip(),

    private val resourcesTable: ResourcesTable = ResourcesTable(personalityTooltip),
    private val resourcesWindow: Window = Utils.createDefaultWindow(TITLE_RESOURCES, resourcesTable.container),

    mechanicTable: MechanicTable = MechanicTable(heroId),
    private val mechanicWindow: Window = Utils.createDefaultWindow(TITLE_MECHANIC, mechanicTable.table),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = Utils.createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf<WindowSelector>(resourcesTable), // ToDo
    selectedTableIndex: Int = 0 // ToDo

) : ScreenUI(stage, heroesTable, tableList, selectedTableIndex) {

    init {
        super.init()
    }

    fun update() {
        resourcesTable.update()
        heroesTable.update()

        resourcesWindow.pack()
        mechanicWindow.pack()
        heroesWindow.pack()
    }

    override fun setWindowPositions() {
        resourcesWindow.setPosition(RESOURCES_WINDOW_POSITION_X, RESOURCES_WINDOW_POSITION_Y)
        mechanicWindow.setPosition(MECHANIC_WINDOW_POSITION_X, MECHANIC_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        personalityTooltip.addToStage(stage)
        stage.addActor(resourcesWindow)
        stage.addActor(mechanicWindow)
        stage.addActor(heroesWindow)
    }

}
