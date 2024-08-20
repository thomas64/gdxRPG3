package nl.t64.cot.screens.loot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.screens.inventory.tooltip.LootSlotTooltip


private const val WINDOW_PADDING_BOTTOM = 10f
private const val LABEL_PADDING_TOP = 15f

class LootUI(resolveLootAndCloseScreen: (Boolean) -> Unit, loot: Loot, title: String) {

    private val tooltip = LootSlotTooltip()
    private val lootSlotsContainer = LootSlotsTable(resolveLootAndCloseScreen, loot, tooltip)
    private val lootWindow: Window = Utils.createDefaultWindow(title, lootSlotsContainer)
        .apply { setWindowPosition() }
    private val buttonLabel = Label(createText(), LabelStyle(BitmapFont(), Color.BLACK))
        .apply { setLabelPosition() }

    fun show(stage: Stage) {
        stage.addActor(lootWindow)
        tooltip.addToStage(stage)
        stage.addActor(buttonLabel)
        lootSlotsContainer.setFocus(stage)
    }

    private fun createText(): String {
        val takeOrBack = if (lootSlotsContainer.isContentEmpty()) "Back" else "Take"
        return if (Utils.isGamepadConnected()) {
            "[A] $takeOrBack      [Select] Tooltip      [B] Back"
        } else {
            "[A] $takeOrBack      [T] Tooltip      [Esc] Back"
        }
    }

    private fun Window.setWindowPosition() {
        setPosition((Gdx.graphics.width / 2f) - (width / 2f),
                    (Gdx.graphics.height / 2f) - (height / 2f) + WINDOW_PADDING_BOTTOM)
    }

    private fun Label.setLabelPosition() {
        setPosition((Gdx.graphics.width / 2f) - (width / 2f),
                    (Gdx.graphics.height / 2f) - (lootWindow.height / 2f) - LABEL_PADDING_TOP)
    }

}
