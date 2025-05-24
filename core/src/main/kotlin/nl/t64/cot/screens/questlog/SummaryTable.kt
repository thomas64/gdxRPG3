package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.FontProvider


private const val WIDTH = -102f
private const val PAD = 20f

internal class SummaryTable {

    private val summary: Label = createLabel()
    val container: Table = createContainer()

    fun populateSummary(quest: QuestGraph) {
        summary.setText(quest.summary)
        container.cells.peek().setActor<Image>(Utils.getFaceImage(quest.entityId, isFlipped = false))
    }

    fun isEmpty(): Boolean {
        return summary.text.isNullOrBlank()
    }

    private fun createLabel(): Label {
        val labelStyle = LabelStyle(FontProvider.spectralExtraBold20, Color.BLACK)
        return Label("", labelStyle).apply { wrap = true }
    }

    private fun createContainer(): Table {
        return Table().apply {
            defaults().top()
            val threeQuartersOfScreenWidth = Gdx.graphics.width * .75f
            columnDefaults(0).width(threeQuartersOfScreenWidth + WIDTH - Constant.FACE_SIZE - PAD).pad(PAD)
            columnDefaults(1).size(Constant.FACE_SIZE)
            background = Utils.createTopBorder()
            add(summary)
            add()
        }
    }

}
