package nl.t64.cot.screens.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.assets.disposeSafely
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.constants.Constant


private const val FONT_PATH = "fonts/spectral_extra_bold_20.ttf"
private const val FONT_BIG_PATH = "fonts/spectral_extra_bold_28.ttf"

private val TRANSPARENT_BLACK = Color(0f, 0f, 0f, 0.8f)
private val TRANSPARENT_WHITE = Color(1f, 1f, 1f, 0.3f)
private val TRANSPARENT_FIREBRICK = Color(Color.FIREBRICK.r, Color.FIREBRICK.g, Color.FIREBRICK.b, 0.5f)
private val TRANSPARENT_FOREST = Color(Color.FOREST.r, Color.FOREST.g, Color.FOREST.b, 0.5f)
private val TRANSPARENT_FACES = Color(1f, 1f, 1f, 0.7f)
private val TRANSPARENT_DEATH = Color(0.25f, 0.25f, 0.25f, 0.75f)

private const val FONT_BIG_SIZE = 28
private const val FONT_SIZE = 20

private const val LINE_HEIGHT = 22f
private const val PADDING = 10f
private const val LABEL_LEFT_MARGIN = 7f
private const val TABLE_WIDTH = PADDING + Constant.FACE_SIZE + Constant.FACE_SIZE
private const val TABLE_HEIGHT = (Constant.FACE_SIZE * PartyContainer.MAXIMUM) + (PADDING * (PartyContainer.MAXIMUM - 1f))
private const val HIGH_X = 0f
private const val LOW_X = -TABLE_WIDTH

private const val BAR_X = 55f
private const val BAR_Y = -7f
private const val BAR_WIDTH = 80f
private const val BAR_HEIGHT = 12f

private const val VELOCITY = 800f

internal class PartyWindow {

    private val party: PartyContainer get() = gameData.party

    private val font = resourceManager.getTrueTypeAsset(FONT_PATH, FONT_SIZE)
    private val fontBig = resourceManager.getTrueTypeAsset(FONT_BIG_PATH, FONT_BIG_SIZE)
    private val shapeRenderer = ShapeRenderer()

    private var xPos = LOW_X
    private var isMovingIn = false
    private var isMovingOut = false

    private val table = Table().apply {
        setSize(TABLE_WIDTH, TABLE_HEIGHT)
        setPosition(0f, (Gdx.graphics.height - TABLE_HEIGHT) / 2f)
        isVisible = false
    }
    private val stage = Stage().apply {
        addActor(table)
    }

    fun dispose() {
        stage.dispose()
        font.disposeSafely()
        fontBig.disposeSafely()
        shapeRenderer.dispose()
    }

    fun showHide() {
        if (!isMovingOut && !isMovingIn) {
            setVisibility()
        }
    }

    private fun setVisibility() {
        if (table.isVisible) {
            isMovingOut = true
        } else {
            table.isVisible = true
            isMovingIn = true
        }
    }

    fun update(dt: Float) {
        handleMovingIn(dt)
        handleMovingOut(dt)
        handleRendering(dt)
    }

    private fun handleMovingIn(dt: Float) {
        if (isMovingIn) {
            xPos = (xPos + VELOCITY * dt).coerceAtMost(HIGH_X)
            isMovingIn = xPos != HIGH_X
        }
    }

    private fun handleMovingOut(dt: Float) {
        if (isMovingOut) {
            xPos = (xPos - VELOCITY * dt).coerceAtLeast(LOW_X)
            isMovingOut = xPos != LOW_X
            if (!isMovingOut) {
                table.isVisible = false
            }
        }
    }

    private fun handleRendering(dt: Float) {
        if (table.isVisible) {
            renderTable()
            stage.act(dt)
            stage.draw()
        }
    }

    private fun renderTable() {
        table.clear()
        party.forEachWithInvertedIndex { invertedIndex, hero ->
            renderFaces(invertedIndex, hero)
            renderFilled(invertedIndex, hero)
            renderLabels(invertedIndex, hero)
            renderLines(invertedIndex)
        }
        renderSquares()
    }

    private fun renderFilled(invertedIndex: Int, hero: HeroItem) {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        renderBackground(invertedIndex)
        shapeRenderer.color = TRANSPARENT_FIREBRICK
        drawBar(invertedIndex, 2f, hero.hpBarWidth)
        shapeRenderer.color = TRANSPARENT_FOREST
        drawBar(invertedIndex, 3f, hero.mpBarWidth)

        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun renderLabels(invertedIndex: Int, hero: HeroItem) {
        renderName(invertedIndex, hero.name)
        renderLabel(invertedIndex, "TXP: ${hero.totalXp}", 0f)
        renderLabel(invertedIndex, "XPI:  ${hero.xpToInvest}", 1f)
        renderLabel(invertedIndex, "HP: ", 2f)
        renderLabel(invertedIndex, "MP: ", 3f)
    }

    private fun renderLines(invertedIndex: Int) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = TRANSPARENT_BLACK

        renderVerticalLine(invertedIndex)
        drawBarOutline(invertedIndex, 2f)
        drawBarOutline(invertedIndex, 3f)

        shapeRenderer.end()
    }

    private fun renderFaces(invertedIndex: Int, hero: HeroItem) {
        val image = Utils.getFaceImage(hero.id)
        image.color = if (hero.isAlive) TRANSPARENT_FACES else TRANSPARENT_DEATH
        image.setPosition(xPos + PADDING, invertedIndex * (Constant.FACE_SIZE + PADDING))
        table.addActor(image)
    }

    private fun renderBackground(invertedIndex: Int) {
        shapeRenderer.color = TRANSPARENT_WHITE
        val x = xPos + PADDING + Constant.FACE_SIZE
        val y = table.y + invertedIndex * (Constant.FACE_SIZE + PADDING)
        shapeRenderer.rect(x, y, Constant.FACE_SIZE, Constant.FACE_SIZE)
    }

    private fun renderVerticalLine(invertedIndex: Int) {
        shapeRenderer.line(
            xPos + PADDING + Constant.FACE_SIZE,
            table.y + invertedIndex * (Constant.FACE_SIZE + PADDING),
            xPos + PADDING + Constant.FACE_SIZE,
            table.y + invertedIndex * (Constant.FACE_SIZE + PADDING) + Constant.FACE_SIZE
        )
    }

    private fun renderName(invertedIndex: Int, heroName: String) {
        val labelStyle = LabelStyle(fontBig, TRANSPARENT_BLACK)
        val heroLabel = Label(heroName, labelStyle)
        heroLabel.setPosition(
            xPos + PADDING + Constant.FACE_SIZE + LABEL_LEFT_MARGIN,
            table.y - (PADDING / 2f) + invertedIndex * (Constant.FACE_SIZE + PADDING) + LINE_HEIGHT
        )
        table.addActor(heroLabel)
    }

    private fun renderLabel(invertedIndex: Int, text: String, offset: Float) {
        val labelStyle = LabelStyle(font, TRANSPARENT_BLACK)
        val label = Label(text, labelStyle)
        label.setPosition(
            xPos + PADDING + Constant.FACE_SIZE + LABEL_LEFT_MARGIN,
            table.y - PADDING + invertedIndex * (Constant.FACE_SIZE + PADDING) - offset * LINE_HEIGHT
        )
        table.addActor(label)
    }

    private fun renderSquares() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = TRANSPARENT_BLACK
        party.forEachIndexToMaximum { index ->
            val x = xPos + PADDING
            val y = table.y + index * (Constant.FACE_SIZE + PADDING)
            val width = TABLE_WIDTH - PADDING
            shapeRenderer.rect(x, y, width, Constant.FACE_SIZE)
        }
        shapeRenderer.end()
    }

    private val HeroItem.hpBarWidth: Float get() = BAR_WIDTH / maximumHp * currentHp
    private val HeroItem.mpBarWidth: Float get() = BAR_WIDTH / maximumMp * currentMp

    private fun drawBarOutline(invertedIndex: Int, linePosition: Float) {
        drawBar(invertedIndex, linePosition, BAR_WIDTH)
    }

    private fun drawBar(invertedIndex: Int, linePosition: Float, barWidth: Float) {
        val x = xPos + PADDING + Constant.FACE_SIZE + BAR_X
        val y = table.y - PADDING + Constant.FACE_SIZE + invertedIndex * (Constant.FACE_SIZE + PADDING) - (linePosition + 2f) * LINE_HEIGHT + BAR_Y
        shapeRenderer.rect(x, y, barWidth, BAR_HEIGHT)
    }

}
