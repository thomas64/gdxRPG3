package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant


private const val SPRITE_TRANSPARENT = "sprites/transparent.png"
private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val WIDTH = -102f
private const val HEIGHT = -152f
private const val PAD_LEFT = 20f

internal class QuestListTable {

    private val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    val questList: List<QuestGraph> = fillList()
    val scrollPane: ScrollPane = fillScrollPane()
    val container: Table = fillContainer()

    private fun fillList(): List<QuestGraph> {
        val spriteTransparent = Sprite(resourceManager.getTextureAsset(SPRITE_TRANSPARENT))
        val listStyle = ListStyle()
        listStyle.font = font
        listStyle.fontColorSelected = Constant.DARK_RED
        listStyle.fontColorUnselected = Color.BLACK
        listStyle.background = SpriteDrawable(spriteTransparent)
        listStyle.selection = SpriteDrawable(spriteTransparent)

        return List<QuestGraph>(listStyle).apply {
            val knownQuests = GdxArray(gameData.quests.getAllKnownQuestsForVisual())
            setItems(knownQuests)
            selectedIndex = -1
        }
    }

    private fun fillScrollPane(): ScrollPane {
        return ScrollPane(questList).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            setScrollingDisabled(true, false)
            setForceScroll(false, false)
            setScrollBarPositions(false, false)
        }
    }

    private fun fillContainer(): Table {
        return Table().apply {
            background = Utils.createTopBorder()
            padLeft(PAD_LEFT)
            val quarterOfScreenWidth = Gdx.graphics.width * .25f
            add(scrollPane).width(quarterOfScreenWidth + WIDTH).height(Gdx.graphics.height + HEIGHT)
        }
    }

}
