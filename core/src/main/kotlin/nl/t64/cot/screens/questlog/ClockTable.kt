package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager


private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val POS_X = 63f
private const val POS_Y = 50f
private const val PAD_LEFT = -20f
private const val WIDTH = -102f - PAD_LEFT
private const val HEIGHT = 70f

internal class ClockTable {

    private val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    private val container: Table = Table().apply { setPosition(POS_X, POS_Y) }

    fun addTo(stage: Stage) {
        stage.addActor(container)
    }

    fun fill() {
        val quarterOfScreenWidth = Gdx.graphics.width * .25f
        val label = createLabel()
        container.clear()
        container.background = Utils.createFullBorderBlack()
        container.add(label).padLeft(PAD_LEFT).width(quarterOfScreenWidth + WIDTH).height(HEIGHT)
        container.pack()
    }

    private fun createLabel(): Label {
        val style = Label.LabelStyle(font, Color.BLACK)
        val currentCycle = gameData.numberOfCycles
        val time = gameData.clock.getTimeOfDayFormatted()

        return if (currentCycle == 1) {
            Label("Time:   $time", style)
        } else {
            Label("Cycle:  $currentCycle    /    Time:  $time", style)
        }.apply { setAlignment(Align.center) }
    }

}
