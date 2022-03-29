package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


private const val DELAY = 0.5f
private const val OFFSET_X = 250f
private const val OFFSET_Y = 0f

open class PersonalityTooltip : BaseTooltip() {

    override fun toggle(notUsedHere: ItemSlot?) {
        val isEnabled = gameData.isTooltipEnabled
        gameData.isTooltipEnabled = !isEnabled
        window.isVisible = !isEnabled
    }

    fun setPosition(table: Table) {
        val titleTable: Table =
            if (table.parent.parent is Window) {
                // table -> container -> window -> titletable (like stats and calcs)
                (table.parent.parent as Window).titleTable
            } else {
                // table -> scrollpane -> container -> window -> titletable (like skills and spells)
                (table.parent.parent.parent as Window).titleTable
            }
        val localPosition = titleTable.localToStageCoordinates(Vector2(OFFSET_X, OFFSET_Y))
        window.setPosition(localPosition.x, localPosition.y)
    }

    fun refresh(personalityItem: PersonalityItem) {
        hide()
        updateDescription(personalityItem)
        window.toFront()
        if (gameData.isTooltipEnabled) {
            window.addAction(Actions.sequence(Actions.delay(DELAY),
                                              Actions.show()))
        }
    }

    private fun updateDescription(personalityItem: PersonalityItem) {
        window.clear()

        val totalScholar = InventoryUtils.getSelectedHero().getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
        val description = getDescription(personalityItem, totalScholar)
        val labelStyle = LabelStyle(BitmapFont(), Color.WHITE)
        val label = Label(description, labelStyle)
        window.add(label)

        window.pack()
    }

    open fun getDescription(personalityItem: PersonalityItem, totalScholar: Int): String {
        return personalityItem.getTotalDescription()
    }

}
