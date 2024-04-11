package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val LINE_HEIGHT = 26f
private const val PADDING = 20f
private const val PADDING_RIGHT = 10f

abstract class BaseTable(private val tooltip: PersonalityTooltip) : WindowSelector {

    protected val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    val container: Table = Table()
    val table: Table = Table(createSkin()).apply {
        defaults().height(LINE_HEIGHT)
        align(Align.topLeft)
        pad(PADDING).padRight(PADDING_RIGHT)
    }
    protected val scrollPane: ScrollPane = ScrollPane(table)

    protected lateinit var selectedHero: HeroItem
    protected var selectedIndex = 0
    protected var hasJustUpdated = true

    override fun setKeyboardFocus(stage: Stage) {
        selectedHero = InventoryUtils.getSelectedHero()
        stage.keyboardFocus = table
        InventoryUtils.setWindowSelected(container)
    }

    override fun deselectCurrentSlot() {
        hideTooltip()
        InventoryUtils.setWindowDeselected(container)
    }

    override fun selectCurrentSlot() {
        hasJustUpdated = true
        selectAnotherSlotWhenIndexBecameOutOfBounds()
    }

    open fun selectAnotherSlotWhenIndexBecameOutOfBounds(): Unit =
        throw IllegalStateException("Implement this method in child if necessary.")

    override fun hideTooltip() {
        hasJustUpdated = false
        tooltip.hide()
    }

    override fun toggleTooltip() {
        if (table.hasContent()) {
            tooltip.setPosition(getTooltipPosition())
            tooltip.toggle(null)
        }
    }

    override fun toggleCompare() {
        // do nothing. tooltips in tables based on BaseTable don't need to toggle compare.
    }

    open fun updateIndex(deltaIndex: Int, size: Int) {
        selectedIndex += deltaIndex
        if (selectedIndex < 0) {
            selectedIndex = size - 1
        } else if (selectedIndex >= size) {
            selectedIndex = 0
        }
        hasJustUpdated = true
        playSe(AudioEvent.SE_MENU_CURSOR)
    }

    fun update() {
        selectedHero = InventoryUtils.getSelectedHero()
        table.clear()
        fillRows()
    }

    open fun fillRows(): Unit = throw IllegalStateException("Implement this method in child if necessary.")

    fun addExtraToTable(totalExtra: Int) {
        if (totalExtra > 0) {
            val label = Label("+$totalExtra", LabelStyle(font, Color.FOREST))
            table.add(label).row()
        } else if (totalExtra < 0) {
            val label = Label(totalExtra.toString(), LabelStyle(font, Color.FIREBRICK))
            table.add(label).row()
        } else {
            table.add("").row()
        }
    }

    fun createImageOf(id: String): Image {
        val textureRegion = resourceManager.getAtlasTexture(id.lowercase())
        return Image(textureRegion).apply {
            setScaling(Scaling.none)
        }
    }

    fun possibleSetSelected(index: Int, personalityTitle: Label, personalityItem: PersonalityItem) {
        if (table.hasKeyboardFocus() && index == selectedIndex) {
            setSelected(personalityTitle, personalityItem)
        }
    }

    fun setSelected(personalityTitle: Label, personalityItem: PersonalityItem) {
        personalityTitle.style.fontColor = Constant.LIGHT_RED
        refreshTooltipOnlyOnce(personalityItem)
    }

    fun getSelected(): Vector2 {
        if (!table.hasKeyboardFocus()) return Vector2.Zero
        return table.cells.asSequence()
            .filter { it.actor is Label }
            .filter { (it.actor as Label).style.fontColor == Constant.LIGHT_RED }
            .map { Vector2(it.actorX, it.actorY) }
            .first()
    }

    private fun refreshTooltipOnlyOnce(personalityItem: PersonalityItem) {
        if (hasJustUpdated) {
            hasJustUpdated = false
            tooltip.refresh(personalityItem) { getTooltipPosition() }
        }
    }

    open fun getTooltipPosition(): Vector2 {
        val scrollDifference = table.height - scrollPane.height
        val x = table.localToScreenCoordinates(getSelected()).x
        val y = getSelected().y + (scrollPane.scrollY - scrollDifference)
        return Vector2(x, y)
    }

    private fun createSkin(): Skin {
        return Skin().apply {
            add("default", LabelStyle(font, Color.BLACK))
        }
    }

    private fun Table.hasContent(): Boolean {
        val firstLineFirstNumber = children[2] as Label
        return children.size > 4
            || firstLineFirstNumber.text.isNotEmpty()
    }

}
