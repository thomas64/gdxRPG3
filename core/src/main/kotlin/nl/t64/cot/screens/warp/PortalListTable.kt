package nl.t64.cot.screens.warp

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.portal.Portal
import nl.t64.cot.constants.Constant


private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val WIDTH = 330f
private const val HEIGHT = 470f
private const val PAD_LEFT = 30f

internal class PortalListTable {

    private val portalListFont: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    val portalList: List<Portal> = createList()
    val scrollPane: ScrollPane = fillScrollPane()
    val container: Table = fillContainer()
    private val noPortalsLabel: Label = createNoPortalsLabel()

    fun populatePortalList(currentMapName: String) {
        val activatedPortals = gameData.portals.getAllActivatedPortalsExcept(currentMapName)
        if (activatedPortals.isEmpty()) {
            container.addActor(noPortalsLabel)
        } else {
            container.removeActor(noPortalsLabel)
            portalList.setItems(GdxArray(activatedPortals))
        }
    }

    private fun createNoPortalsLabel(): Label {
        val labelStyle = Label.LabelStyle(portalListFont, Color.BLACK)
        return Label("No other portals activated.", labelStyle)
            .apply {
                y = container.height / 1.5f
                x = (container.width / 2f) - (width / 2f)
            }
    }

    private fun createList(): List<Portal> {
        return List(List.ListStyle().apply {
            font = portalListFont
            fontColorSelected = Constant.DARK_RED
            fontColorUnselected = Color.BLACK
            selection = Utils.createFullBorder()
            selection.leftWidth = PAD_LEFT
        })
    }

    private fun fillScrollPane(): ScrollPane {
        return ScrollPane(portalList).apply {
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
            add(scrollPane).width(WIDTH).height(HEIGHT)
        }
    }

}
