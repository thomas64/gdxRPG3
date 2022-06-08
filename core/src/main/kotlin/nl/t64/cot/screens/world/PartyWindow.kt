package nl.t64.cot.screens.world

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
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.constants.Constant


private const val FONT_PATH = "fonts/spectral_extra_bold_20.ttf"
private const val FONT_BIG_PATH = "fonts/spectral_extra_bold_28.ttf"
private val TRANSPARENT_BLACK = Color(0f, 0f, 0f, 0.8f)
private val TRANSPARENT_WHITE = Color(1f, 1f, 1f, 0.3f)
private val TRANSPARENT_FACES = Color(1f, 1f, 1f, 0.7f)
private val TRANSPARENT_DEATH = Color(0x3f3f3fc0)

private const val FONT_BIG_SIZE = 28
private const val FONT_SIZE = 20

private const val LINE_HEIGHT = 22f
private const val PADDING = 10f
private const val PADDING_SMALL = 5f
private const val PADDING_NAME = 7f
private const val PADDING_LEVEL = 12f
private const val PADDING_LINE = PADDING + PADDING_SMALL
private const val FACE_Y = 90f
private const val TABLE_WIDTH = Constant.FACE_SIZE * PartyContainer.MAXIMUM + PADDING * (PartyContainer.MAXIMUM - 1f)
private const val TABLE_HEIGHT = FACE_Y + Constant.FACE_SIZE
private const val HIGH_Y = 0f
private const val LOW_Y = -TABLE_HEIGHT

private const val BAR_X = 50f
private const val BAR_Y = -4f
private const val BAR_WIDTH = 85f
private const val BAR_HEIGHT = 12f

private const val VELOCITY = 800f

internal class PartyWindow {

    private lateinit var party: PartyContainer

    private val font = resourceManager.getTrueTypeAsset(FONT_PATH, FONT_SIZE)
    private val fontBig = resourceManager.getTrueTypeAsset(FONT_BIG_PATH, FONT_BIG_SIZE)
    private val shapeRenderer = ShapeRenderer()

    private var yPos = LOW_Y
    private var isMovingUp = false
    private var isMovingDown = false

    private val table = Table().apply {
        setSize(TABLE_WIDTH, TABLE_HEIGHT)
        setPosition((Gdx.graphics.width - TABLE_WIDTH) / 2f, 0f)
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
        if (!isMovingDown && !isMovingUp) {
            setVisibility()
        }
    }

    private fun setVisibility() {
        if (table.isVisible) {
            isMovingDown = true
        } else {
            table.isVisible = true
            isMovingUp = true
        }
    }

    fun update(dt: Float) {
        handleMovingUp(dt)
        handleMovingDown(dt)
        handleRendering(dt)
    }

    private fun handleMovingUp(dt: Float) {
        if (isMovingUp) {
            yPos += VELOCITY * dt
            if (yPos >= HIGH_Y) {
                yPos = HIGH_Y
                isMovingUp = false
            }
        }
    }

    private fun handleMovingDown(dt: Float) {
        if (isMovingDown) {
            yPos -= VELOCITY * dt
            if (yPos <= LOW_Y) {
                yPos = LOW_Y
                isMovingDown = false
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
        party = gameData.party // todo, hij hoeft de party niet bij elke fps op te halen.
        table.clear()
        renderBackgrounds()
        renderSquares()
        (0 until party.size).forEach {
            renderHorizontalLine(it)
            renderFace(it)
            renderName(it)
            renderLevelLabel(it)
            renderHpLabel(it)
            renderHpBar(it)
            renderXpLabel(it)
            renderXpBar(it)
        }
    }

    private fun renderBackgrounds() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = TRANSPARENT_WHITE
        (0 until party.size).forEach {
            shapeRenderer.rect(
                table.x + it * Constant.FACE_SIZE + it * PADDING,
                yPos + PADDING,
                Constant.FACE_SIZE,
                FACE_Y - PADDING
            )
        }
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun renderSquares() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = TRANSPARENT_BLACK
        (0 until PartyContainer.MAXIMUM).forEach {
            shapeRenderer.rect(
                table.x + it * Constant.FACE_SIZE + it * PADDING,
                yPos + PADDING,
                Constant.FACE_SIZE,
                TABLE_HEIGHT - PADDING
            )
        }
        shapeRenderer.end()
    }

    private fun renderHorizontalLine(i: Int) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = TRANSPARENT_BLACK
        shapeRenderer.line(
            table.x + i * Constant.FACE_SIZE + i * PADDING,
            yPos + FACE_Y,
            table.x + i * Constant.FACE_SIZE + i * PADDING + Constant.FACE_SIZE,
            yPos + FACE_Y
        )
        shapeRenderer.end()
    }

    private fun renderFace(i: Int) {
        val hero = party.getHero(i)
        val image = Utils.getFaceImage(hero.id)
        image.color = TRANSPARENT_FACES
        if (!hero.isAlive) image.color = TRANSPARENT_DEATH
        image.setPosition(i * Constant.FACE_SIZE + i * PADDING, yPos + FACE_Y)
        table.addActor(image)
    }

    private fun renderName(i: Int) {
        val labelStyle = LabelStyle(fontBig, TRANSPARENT_BLACK)
        val heroName = party.getHero(i).name
        val heroLabel = Label(heroName, labelStyle)
        heroLabel.setPosition(
            i * Constant.FACE_SIZE + i * PADDING + PADDING_SMALL,
            yPos + FACE_Y - PADDING_NAME
        )
        table.addActor(heroLabel)
    }

    private fun renderLevelLabel(i: Int) {
        val labelStyle = LabelStyle(font, TRANSPARENT_BLACK)
        val heroLevel = party.getHero(i).getLevel()
        val levelLabel = Label("Level: $heroLevel", labelStyle)
        levelLabel.setPosition(
            i * Constant.FACE_SIZE + i * PADDING + PADDING_SMALL,
            yPos + FACE_Y - 1f * LINE_HEIGHT - PADDING_LEVEL
        )
        table.addActor(levelLabel)
    }

    private fun renderHpLabel(i: Int) {
        val labelStyle = LabelStyle(font, TRANSPARENT_BLACK)
        val hpLabel = Label("HP: ", labelStyle)
        hpLabel.setPosition(
            i * Constant.FACE_SIZE + i * PADDING + PADDING_SMALL,
            yPos + FACE_Y - 2f * LINE_HEIGHT - PADDING_LINE
        )
        table.addActor(hpLabel)
    }

    private fun renderHpBar(i: Int) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val hero = party.getHero(i)
        val hpStats = hero.getAllHpStats()
        val color = Utils.getHpColor(hpStats)
        shapeRenderer.color = color
        val barWidth = BAR_WIDTH / hero.getMaximumHp() * hero.getCurrentHp()
        renderBar(i, 2f, barWidth)
        shapeRenderer.end()
        renderBarOutline(i, 2f)
    }

    private fun renderXpLabel(i: Int) {
        val labelStyle = LabelStyle(font, TRANSPARENT_BLACK)
        val xpLabel = Label("XP: ", labelStyle)
        xpLabel.setPosition(
            i * Constant.FACE_SIZE + i * PADDING + PADDING_SMALL,
            yPos + FACE_Y - 3f * LINE_HEIGHT - PADDING_LINE
        )
        table.addActor(xpLabel)
    }

    private fun renderXpBar(i: Int) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.MAROON
        renderBar(i, 3f, calculateXpBarWidth(i))
        shapeRenderer.end()
        renderBarOutline(i, 3f)
    }

    private fun calculateXpBarWidth(i: Int): Float {
        val hero = party.getHero(i)
        val maxXp = hero.xpDeltaBetweenLevels
        val currentXp = maxXp - hero.xpNeededForNextLevel
        return BAR_WIDTH / maxXp * currentXp
    }

    private fun renderBarOutline(i: Int, linePosition: Float) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = TRANSPARENT_BLACK
        renderBar(i, linePosition, BAR_WIDTH)
        shapeRenderer.end()
    }

    private fun renderBar(partyNumber: Int, linePosition: Float, barWidth: Float) {
        shapeRenderer.rect(
            table.x + partyNumber * Constant.FACE_SIZE + partyNumber * PADDING + BAR_X,
            yPos + FACE_Y - linePosition * LINE_HEIGHT + BAR_Y,
            barWidth,
            BAR_HEIGHT
        )
    }

}
