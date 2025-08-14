package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 205f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class SpellsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    private val allAbilities: List<AbilityItem> get() = selectedHero.getAllAbilities().filterNot { it.getTotalDescription().isBlank() }
    private var deltaIndex = 0

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)

        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, allAbilities.size) })

        super.update()
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= allAbilities.size) {
            selectedIndex = allAbilities.size - 1
        }
    }

    override fun updateIndex(deltaIndex: Int, size: Int) {
        this.deltaIndex = deltaIndex
        super.updateIndex(deltaIndex, size)
    }

    override fun fillRows() {
        allAbilities.forEachIndexed { index, ability -> fillRow(ability, index) }

        if (deltaIndex != 0) {
            scrollScrollPane()
            deltaIndex = 0
        }
    }

    private fun fillRow(ability: AbilityItem, index: Int) {
        table.add(createImageOf(ability.imageId))
        val abilityName = Label(ability.name, LabelStyle(font, Color.BLACK))
        table.add(abilityName).padLeft(SECOND_COLUMN_PAD_LEFT).row()
        super.possibleSetSelected(index, abilityName, ability)

    }

    private fun scrollScrollPane() {
        if (!table.hasKeyboardFocus()) return
        val y = getSelected().y
        val rowScroll = if (y < table.height / 2f) {
            -1f * ROW_HEIGHT
        } else {
            5f * ROW_HEIGHT
        }
        val scrollDifference = table.height - scrollPane.height
        val scrollY = scrollPane.height - ((y + rowScroll) - scrollDifference)
        scrollPane.scrollTo(0f, scrollY, 0f, 0f)
    }

}
