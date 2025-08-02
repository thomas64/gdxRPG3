package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.SuperEnum
import nl.t64.cot.components.party.inventory.AttributeState
import nl.t64.cot.components.party.inventory.InventoryDescription
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.screens.inventory.itemslot.InventoryImage


private const val COLUMN_SPACING = 20f
private val ORANGE = Color(-0x6fff01)

fun Table.addPossibleDescription(inventoryImage: InventoryImage) {
    if (inventoryImage.inventoryItem.description.isEmpty()) return

    this.add(createEmptyLine()).row()
    val description = inventoryImage.inventoryItem.description.joinToString(System.lineSeparator())
    this.add(createLabel(description, Color.WHITE)).colspan(2)
}

fun Table.addDescriptionLine(line: InventoryDescription, labelStyle: LabelStyle?) {
    this.add(Label(getKey(line), labelStyle)).spaceRight(COLUMN_SPACING)
    this.add(Label(getValue(line), labelStyle)).row()
}

fun MutableList<InventoryDescription>.removeUnnecessaryAttributes() {
    this.removeBuy()
    this.removeSell()
}

fun MutableList<InventoryDescription>.removeBuy() {
    this.removeAll {
        it.key in listOf(Constant.DESCRIPTION_KEY_BUY,
                         Constant.DESCRIPTION_KEY_BUY_PIECE,
                         Constant.DESCRIPTION_KEY_BUY_TOTAL)
    }
}

fun MutableList<InventoryDescription>.removeSell() {
    this.removeAll {
        it.key in listOf(Constant.DESCRIPTION_KEY_SELL,
                         Constant.DESCRIPTION_KEY_SELL_PIECE,
                         Constant.DESCRIPTION_KEY_SELL_TOTAL)
    }
}

fun createSingleLabelStyle(attribute: InventoryDescription): LabelStyle {
    return if (isBuyOrSellValue(attribute)) {
        createLabelStyle(Color.GOLD)
    } else when (attribute.compare) {
        AttributeState.SAME -> createLabelStyle(Color.WHITE)
        AttributeState.CANNOT_USE -> createLabelStyle(Color.RED)
        else -> throw IllegalArgumentException("Comparing to hero cannot be LESS or MORE.")
    }
}

fun createLeftLabelStyle(attribute: InventoryDescription): LabelStyle {
    return if (isBuyOrSellValue(attribute)) {
        createLabelStyle(Color.GOLD)
    } else when (attribute.compare) {
        AttributeState.CANNOT_USE -> createLabelStyle(Color.RED)
        AttributeState.SAME -> createLabelStyle(Color.WHITE)
        AttributeState.LESS -> createLabelStyle(ORANGE)
        AttributeState.MORE -> createLabelStyle(Color.LIME)
    }
}

fun createRightLabelStyle(attribute: InventoryDescription): LabelStyle {
    return when {
        isBuyOrSellValue(attribute) -> createLabelStyle(Color.GOLD)
        else -> createLabelStyle(Color.WHITE)
    }
}

fun createEmptyLine(): Label {
    return createLabel("", Color.CLEAR)
}

fun createLabel(text: String, color: Color): Label {
    return Label(text, createLabelStyle(color))
}

private fun createLabelStyle(color: Color): LabelStyle {
    return LabelStyle(FontProvider.default, color)
}

private fun getKey(attribute: InventoryDescription): String {
    return when (attribute.key) {
        is SuperEnum -> attribute.key.title
        else -> attribute.key.toString()
    }
}

private fun getValue(descriptionLine: InventoryDescription): String {
    return when {
        descriptionLine.value is SkillItemId -> descriptionLine.value.title
        descriptionLine.key == CalcAttributeId.BASE_HIT -> "${descriptionLine.value}%"
        else -> descriptionLine.value.toString()
    }
}

private fun isBuyOrSellValue(attribute: InventoryDescription): Boolean {
    return attribute.key in listOf(Constant.DESCRIPTION_KEY_BUY_TOTAL,
                                   Constant.DESCRIPTION_KEY_SELL_TOTAL,
                                   Constant.DESCRIPTION_KEY_BUY_PIECE,
                                   Constant.DESCRIPTION_KEY_SELL_PIECE,
                                   Constant.DESCRIPTION_KEY_BUY,
                                   Constant.DESCRIPTION_KEY_SELL)
}
