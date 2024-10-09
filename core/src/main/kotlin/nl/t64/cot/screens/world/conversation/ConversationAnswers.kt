package nl.t64.cot.screens.world.conversation

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.components.conversation.ConversationChoice
import nl.t64.cot.constants.Constant
import nl.t64.cot.toDrawable


private const val SCROLL_PANE_LINE_PAD = -4f
private const val PAD = 25f

class ConversationAnswers(
    private val font: BitmapFont
) : List<ConversationChoice>(createAnswersStyle(font)) {

    private val selectedIndexHistory: MutableMap<GdxArray<ConversationChoice>, Int> = mutableMapOf()

    companion object {
        private fun createAnswersStyle(font: BitmapFont, drawable: Drawable = BaseDrawable()): ListStyle {
            return ListStyle(font, Constant.DARK_RED, Color.BLACK, drawable).apply {
                selection.leftWidth = PAD
                selection.rightWidth = PAD
            }
        }
    }

    fun populateChoices(choices: GdxArray<ConversationChoice>) {
        if (!selectedIndexHistory.containsKey(choices)) {
            selectedIndexHistory[choices] = -1
        }
        super.setItems(choices)
        tryToSetLastChosenSelectedIndex(choices)
        setStyleBasedOnContent(choices)
    }

    fun storeSelectedIndex() {
        selectedIndexHistory[items] = selectedIndex
    }

    fun clearSelectedIndexHistory() {
        selectedIndexHistory.clear()
    }

    override fun drawItem(
        batch: Batch, font: BitmapFont, index: Int, item: ConversationChoice, x: Float, y: Float, width: Float
    ): GlyphLayout {
        font.color = when {
            index == selectedIndex
                && item.isMeetingCondition()
                && !item.hasBeenSelectedEarlier -> style.fontColorSelected
            !item.isMeetingCondition() -> Color.GRAY
            item.hasBeenSelectedEarlier -> Color.TEAL
            else -> style.fontColorUnselected
        }
        return super.drawItem(batch, font, index, item, x, y, width)
    }

    private fun tryToSetLastChosenSelectedIndex(choices: GdxArray<ConversationChoice>) {
        if (choices.size == 1) {
            selectedIndex = 0
        } else {
            selectedIndex = selectedIndexHistory[choices]!!
        }
    }

    private fun setStyleBasedOnContent(choices: GdxArray<ConversationChoice>) {
        val drawable = if (choices[0].isDefault()) {
            Color.CLEAR.toDrawable()
        } else {
            Utils.createFullBorderBlack()
        }.apply {
            topHeight = SCROLL_PANE_LINE_PAD
            bottomHeight = SCROLL_PANE_LINE_PAD
        }
        style = createAnswersStyle(font, drawable)
    }

}
