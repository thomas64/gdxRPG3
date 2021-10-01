package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


private const val DELAY = 0.5f

open class PersonalityTooltip : BaseTooltip() {

    private var x: Float = 0f
    private lateinit var y: () -> Float

    override fun toggle(notUsedHere: ItemSlot?) {
        val isEnabled = gameData.isTooltipEnabled
        gameData.isTooltipEnabled = !isEnabled
        window.isVisible = !isEnabled
    }

    fun setPosition(x: Float, y: () -> Float) {
        this.x = x
        this.y = y
    }

    fun refresh(label: Label, personalityItem: PersonalityItem) {
        hide()
        setupTooltip(label, personalityItem)
        if (gameData.isTooltipEnabled) {
            window.addAction(Actions.sequence(Actions.delay(DELAY),
                                              Actions.show()))
        }
    }

    private fun setupTooltip(label: Label, personalityItem: PersonalityItem) {
        val localCoords = Vector2(label.x, label.y)
        label.localToStageCoordinates(localCoords)
        updateDescription(personalityItem)
        window.setPosition(localCoords.x + x, localCoords.y + y.invoke())
        window.toFront()
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
        return personalityItem.getDescription(totalScholar)
    }

}
