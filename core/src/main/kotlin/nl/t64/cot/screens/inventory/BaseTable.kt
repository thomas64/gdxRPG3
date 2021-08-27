package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.screens.inventory.InventoryUtils.getSelectedHero
import nl.t64.cot.screens.inventory.InventoryUtils.setWindowDeselected
import nl.t64.cot.screens.inventory.InventoryUtils.setWindowSelected
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.tooltip.BaseTooltip
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val LINE_HEIGHT = 26f
private const val PADDING = 20f
private const val PADDING_RIGHT = 10f

abstract class BaseTable(val tooltip: PersonalityTooltip) : WindowSelector {

    val container: Table = Table()
    val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    val table: Table = Table(createSkin()).apply {
        defaults().height(LINE_HEIGHT)
        pad(PADDING).padRight(PADDING_RIGHT)
    }

    lateinit var selectedHero: HeroItem
    var selectedIndex = 0
    var hasJustUpdated = true

    override fun setKeyboardFocus(stage: Stage) {
        selectedHero = getSelectedHero()
        stage.keyboardFocus = table
        setWindowSelected(container)
    }

    override fun getCurrentSlot(): ItemSlot? {
        return null    // not used in BaseTables.
    }

    override fun getCurrentTooltip(): BaseTooltip {
        return tooltip
    }

    override fun deselectCurrentSlot() {
        hideTooltip()
        setWindowDeselected(container)
    }

    override fun selectCurrentSlot() {
        hasJustUpdated = true
    }

    override fun hideTooltip() {
        hasJustUpdated = false
        tooltip.hide()
    }

    fun update() {
        selectedHero = getSelectedHero()
        table.clear()
        fillRows()
    }

    protected abstract fun fillRows()

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

    private fun createSkin(): Skin {
        return Skin().apply {
            add("default", LabelStyle(font, Color.BLACK))
        }
    }

}
