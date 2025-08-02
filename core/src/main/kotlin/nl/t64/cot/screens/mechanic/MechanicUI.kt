package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.WindowSelector


private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f
private const val MECHANIC_WINDOW_POSITION_X = 63f
private const val MECHANIC_WINDOW_POSITION_Y = 50f
private const val REPAIR_WINDOW_POSITION_X = 550f
private const val REPAIR_WINDOW_POSITION_Y = 50f
private const val CRAFT_WINDOW_POSITION_X = 990f
private const val CRAFT_WINDOW_POSITION_Y = 50f
private const val RESOURCES_WINDOW_POSITION_X = 1519f
private const val RESOURCES_WINDOW_POSITION_Y = 394f

private const val TITLE_HEROES = "   Heroes"
private const val TITLE_MECHANIC = "   Mechanic"
private const val TITLE_REPAIR = "   Repair"
private const val TITLE_CRAFT = "   Craft"
private const val TITLE_RESOURCES = "   Resources"

internal class MechanicUI(
    stage: Stage,
    heroId: String,

    private val resourcesTable: ResourcesTable = ResourcesTable(),
    private val resourcesWindow: Window = Utils.createDefaultWindow(TITLE_RESOURCES, resourcesTable.container),

    private val craftTooltip: MechanicCraftTooltip = MechanicCraftTooltip(),
    private val craftTable: CraftTable = CraftTable(heroId, craftTooltip),
    private val craftWindow: Window = Utils.createDefaultWindow(TITLE_CRAFT, craftTable.container),

    private val repairTooltip: MechanicRepairTooltip = MechanicRepairTooltip(),
    private val repairTable: RepairTable = RepairTable(heroId, repairTooltip),
    private val repairWindow: Window = Utils.createDefaultWindow(TITLE_REPAIR, repairTable.container),

    mechanicTable: MechanicTable = MechanicTable(heroId),
    private val mechanicWindow: Window = Utils.createDefaultWindow(TITLE_MECHANIC, mechanicTable.table),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = Utils.createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf<WindowSelector>(repairTable, craftTable),
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
        craftTable.update()
        repairTable.update()
        heroesTable.update()

        resourcesWindow.pack()
        craftWindow.pack()
        repairWindow.pack()
        mechanicWindow.pack()
        heroesWindow.pack()
    }

    fun stopTablesScrolling() {
        craftTable.stopScrolling()
        repairTable.stopScrolling()
    }

    override fun setWindowPositions() {
        resourcesWindow.setPosition(RESOURCES_WINDOW_POSITION_X, RESOURCES_WINDOW_POSITION_Y)
        craftWindow.setPosition(CRAFT_WINDOW_POSITION_X, CRAFT_WINDOW_POSITION_Y)
        repairWindow.setPosition(REPAIR_WINDOW_POSITION_X, REPAIR_WINDOW_POSITION_Y)
        mechanicWindow.setPosition(MECHANIC_WINDOW_POSITION_X, MECHANIC_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        craftTooltip.addToStage(stage)
        repairTooltip.addToStage(stage)
        stage.addActor(resourcesWindow)
        stage.addActor(craftWindow)
        stage.addActor(repairWindow)
        stage.addActor(mechanicWindow)
        stage.addActor(heroesWindow)
    }

}
