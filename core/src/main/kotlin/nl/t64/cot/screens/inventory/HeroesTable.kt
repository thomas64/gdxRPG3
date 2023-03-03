package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.constants.Constant
import nl.t64.cot.disposeAndClear
import nl.t64.cot.toTexture


private const val FONT_PATH = "fonts/spectral_extra_bold_20.ttf"
private const val FONT_BIG_PATH = "fonts/spectral_extra_bold_28.ttf"
private const val FONT_BIG_SIZE = 28
private const val FONT_SIZE = 20
private const val STATS_COLUMN_WIDTH = 134f
private const val STATS_COLUMN_PAD = 10f
private const val BAR_ROW_HEIGHT = 32f
private const val BAR_HEIGHT = 18f
private const val BAR_PAD_TOP = 7f

class HeroesTable {

    private val nameStyle: LabelStyle
    private val levelStyle: LabelStyle
    private val party: PartyContainer = gameData.party
    val heroes = Table().apply {
        background = Utils.createTopBorder()
    }
    private val texturesToDispose: MutableSet<Texture> = mutableSetOf()

    init {
        val font = resourceManager.getTrueTypeAsset(FONT_PATH, FONT_SIZE)
        val fontBig = resourceManager.getTrueTypeAsset(FONT_BIG_PATH, FONT_BIG_SIZE)
        nameStyle = LabelStyle(fontBig, Color.BLACK)
        levelStyle = LabelStyle(font, Color.BLACK)
    }

    fun update() {
        texturesToDispose.disposeAndClear()
        heroes.clear()
        party.getAllHeroes().forEach {
            createFace(it)
            createStats(it)
        }
    }

    private fun createFace(hero: HeroItem) {
        val stack = Stack()
        addPossibleGrayBackgroundTo(stack, hero)
        addFaceImageTo(stack, hero)
        heroes.add(stack)
    }

    private fun addFaceImageTo(stack: Stack, hero: HeroItem) {
        val faceImage = Utils.getFaceImage(hero.id)
        if (!hero.isAlive) faceImage.color = Color.DARK_GRAY
        stack.add(faceImage)
    }

    private fun createStats(hero: HeroItem) {
        val statsTable = createStatsTable(hero)
        addNameLabelTo(statsTable, hero)
        addLevelLabelTo(statsTable, hero)
        addHpLabelTo(statsTable, hero)
        addHpBarTo(statsTable, hero)

        val stack = Stack()
        addPossibleGrayBackgroundTo(stack, hero)
        stack.add(statsTable)
        heroes.add(stack)
    }

    private fun createStatsTable(hero: HeroItem): Table {
        return Table().apply {
            defaults().align(Align.left).top()
            columnDefaults(0).width(STATS_COLUMN_PAD)
            columnDefaults(1).width(STATS_COLUMN_WIDTH)
            columnDefaults(2).width(STATS_COLUMN_PAD)
            if (!party.isHeroLast(hero)) background = Utils.createRightBorder()
        }
    }

    private fun addPossibleGrayBackgroundTo(stack: Stack, hero: HeroItem) {
        if (hero.hasSameIdAs(InventoryUtils.getSelectedHero())) {
            stack.add(Constant.GRAY.toImage())
        }
    }

    private fun addNameLabelTo(statsTable: Table, hero: HeroItem) {
        statsTable.add(Label("", nameStyle))
        statsTable.add(Label(hero.name, nameStyle))
        statsTable.add(Label("", nameStyle)).row()
    }

    private fun addLevelLabelTo(statsTable: Table, hero: HeroItem) {
        statsTable.add(Label("", levelStyle))
        statsTable.add(Label("Level:   " + hero.getLevel(), levelStyle))
        statsTable.add(Label("", levelStyle)).row()
    }

    private fun addHpLabelTo(statsTable: Table, hero: HeroItem) {
        statsTable.add(Label("", levelStyle))
        statsTable.add(Label("HP:  " + hero.getCurrentHp() + "/ " + hero.getMaximumHp(), levelStyle))
        statsTable.add(Label("", levelStyle))
    }

    private fun addHpBarTo(statsTable: Table, hero: HeroItem) {
        statsTable.row().height(BAR_ROW_HEIGHT)
        statsTable.add(Label("", levelStyle))
        statsTable.add(createHpBar(hero)).height(BAR_HEIGHT).padTop(BAR_PAD_TOP)
        statsTable.add(Label("", levelStyle))
    }

    private fun createHpBar(hero: HeroItem): Stack {
        return Stack().apply {
            add(createFill(hero))
            add(Image(Utils.createFullBorder()))
        }
    }

    private fun createFill(hero: HeroItem): Image {
        val hpStats = hero.getAllHpStats()
        val color = Utils.getHpColor(hpStats)
        return color.toImage().apply {
            setScaling(Scaling.stretchY)
            align = Align.left
        }.also { it.drawable.minWidth = (STATS_COLUMN_WIDTH / hero.getMaximumHp()) * hero.getCurrentHp() }
    }

    private fun Color.toImage(): Image {
        return Image(toTexture().also { texturesToDispose.add(it) })
    }

}
