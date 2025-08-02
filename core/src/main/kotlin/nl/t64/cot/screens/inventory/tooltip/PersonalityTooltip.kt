package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


private const val DELAY = 0.5f
private const val OFFSET_X = 105f
private const val OFFSET_Y = 95f

open class PersonalityTooltip : BaseTooltip() {

    override fun toggle(notUsedHere: ItemSlot?) {
        val isEnabled = gameData.isTooltipEnabled
        gameData.isTooltipEnabled = !isEnabled
        window.isVisible = !isEnabled
    }

    fun refresh(personalityItem: PersonalityItem, getUpdatedPosition: () -> Vector2) {
        hide()
        updateDescription(personalityItem)
        show(getUpdatedPosition)
    }

    protected fun show(getUpdatedPosition: () -> Vector2) {
        window.toFront()
        if (gameData.isTooltipEnabled) {
            window.addAction(Actions.sequence(Actions.delay(DELAY),
                                              Actions.run { setPosition(getUpdatedPosition.invoke()) },
                                              Actions.show()))
        }
    }

    fun setPosition(newPosition: Vector2) {
        window.setPosition(newPosition.x + OFFSET_X, newPosition.y + OFFSET_Y)
    }

    private fun updateDescription(personalityItem: PersonalityItem) {
        window.clear()
        window.add(createLabel(getDescription(personalityItem), Color.WHITE))
        window.pack()
    }

    open fun getDescription(personalityItem: PersonalityItem): String {
        return personalityItem.getTotalDescription()
    }

}
