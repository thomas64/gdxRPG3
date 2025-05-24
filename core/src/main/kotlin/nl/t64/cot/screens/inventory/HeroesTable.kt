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
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.constants.Constant
import nl.t64.cot.disposeAndClear
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.toTexture


private const val STATS_COLUMN_WIDTH = 134f
private const val STATS_COLUMN_PAD = 10f
private const val EMPTY_ROW_HEIGHT = 6f
private const val NAME_ROW_HEIGHT = 38f
private const val TEXT_ROW_HEIGHT = 18f
private const val BAR_ROW_HEIGHT = 26f
private const val BAR_HEIGHT = 18f

class HeroesTable {

    private val nameStyle = LabelStyle(FontProvider.spectralExtraBold28, Color.BLACK)
    private val textStyle = LabelStyle(FontProvider.spectralExtraBold20, Color.BLACK)
    private val party: PartyContainer = gameData.party
    val heroes = Table().apply { background = Utils.createTopBorder() }
    private val texturesToDispose: MutableSet<Texture> = mutableSetOf()

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
        if (hero.isDead) faceImage.color = Color.DARK_GRAY
        stack.add(faceImage)
    }

    private fun createStats(hero: HeroItem) {
        val statsTable = createStatsTable(hero)
        addNameLabelTo(statsTable, hero)
        addEmptyLine(statsTable)
        addHpLabelTo(statsTable, hero)
        addHpBarTo(statsTable, hero)
        addEmptyLine(statsTable)
        addSpLabelTo(statsTable, hero)
        addSpBarTo(statsTable, hero)
        addEmptyLine(statsTable)

        val stack = Stack()
        addPossibleGrayBackgroundTo(stack, hero)
        stack.add(statsTable)
        heroes.add(stack)
    }

    private fun createStatsTable(hero: HeroItem): Table {
        return Table().apply {
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

    private fun addEmptyLine(statsTable: Table) {
        statsTable.row().height(EMPTY_ROW_HEIGHT)
        statsTable.add(Label("", nameStyle))
        statsTable.add(Label("", nameStyle))
        statsTable.add(Label("", nameStyle)).row()
    }

    private fun addNameLabelTo(statsTable: Table, hero: HeroItem) {
        statsTable.row().height(NAME_ROW_HEIGHT)
        statsTable.add(Label("", nameStyle))
        statsTable.add(Label(hero.name, nameStyle))
        statsTable.add(Label("", nameStyle)).row()
    }

    private fun addHpLabelTo(statsTable: Table, hero: HeroItem) {
        statsTable.row().height(TEXT_ROW_HEIGHT)
        statsTable.add(Label("", textStyle))
        statsTable.add(Label("HP:  " + hero.currentHp + "/ " + hero.maximumHp, textStyle))
        statsTable.add(Label("", textStyle)).row()
    }

    private fun addHpBarTo(statsTable: Table, hero: HeroItem) {
        statsTable.row().height(BAR_ROW_HEIGHT)
        statsTable.add(Label("", textStyle))
        statsTable.add(createHpBar(hero)).height(BAR_HEIGHT).left().center()
        statsTable.add(Label("", textStyle)).row()
    }

    private fun addSpLabelTo(statsTable: Table, hero: HeroItem) {
        statsTable.row().height(TEXT_ROW_HEIGHT)
        statsTable.add(Label("", textStyle))
        statsTable.add(Label("SP:  " + hero.currentSp + "/ " + hero.maximumSp, textStyle))
        statsTable.add(Label("", textStyle)).row()
    }

    private fun addSpBarTo(statsTable: Table, hero: HeroItem) {
        statsTable.row().height(BAR_ROW_HEIGHT)
        statsTable.add(Label("", textStyle))
        statsTable.add(createSpBar(hero)).height(BAR_HEIGHT).left().center()
        statsTable.add(Label("", textStyle)).row()
    }

    private fun createHpBar(hero: HeroItem): Stack {
        return Stack().apply {
            add(createHpFill(hero))
            add(Image(Utils.createFullBorderBlack()))
        }
    }

    private fun createSpBar(hero: HeroItem): Stack {
        return Stack().apply {
            add(createSpFill(hero))
            add(Image(Utils.createFullBorderBlack()))
        }
    }

    private fun createHpFill(hero: HeroItem): Image {
        return Utils.getHpColor(hero.currentHp, hero.maximumHp).toImage().apply {
            setScaling(Scaling.stretchY)
            align = Align.left
            drawable.minWidth = (STATS_COLUMN_WIDTH / hero.maximumHp) * hero.currentHp
        }
    }

    private fun createSpFill(hero: HeroItem): Image {
        return Color.ROYAL.toImage().apply {
            setScaling(Scaling.stretchY)
            align = Align.left
            drawable.minWidth = (STATS_COLUMN_WIDTH / hero.maximumSp) * hero.currentSp
        }
    }

    private fun Color.toImage(): Image {
        return Image(toTexture().also { texturesToDispose.add(it) })
    }

}
