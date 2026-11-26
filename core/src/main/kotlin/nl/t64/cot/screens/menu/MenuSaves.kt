package nl.t64.cot.screens.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.gamestate.INVALID_PROFILE_VIEW
import nl.t64.cot.gamestate.SaveFile
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.toDrawable
import kotlin.concurrent.thread
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


private const val TITLE_LABEL = "Load save"
private const val MENU_ITEM_LOAD = "Load"
private const val MENU_ITEM_BACK = "Back"

private val LOAD_MESSAGE = """
    All progress after the last save will be lost.
    Are you sure you want to load this save?""".trimIndent()

private const val MENU_X = 604f
private const val TITLE_SPACE_BOTTOM = 45f
private const val BUTTON_SPACE_TOP = 10f
private const val BUTTON_SPACE_RIGHT = 20f

private const val NUMBER_OF_ITEMS = 2
private const val EXIT_INDEX = 1

class MenuSavesMain : MenuSaves() {
    override val titleLogo: Texture = resourceManager.getTextureAsset(TITLE_LOGO_B)
    override val fontColor: Color = Color.BLACK
    override val selectColor: Color = Constant.LIGHT_RED
    override val backScreen: ScreenType = ScreenType.MENU_LOAD_MAIN

    override fun loadSelectedSave() {
        fadeBeforeOpenWorldScreen(selectedSaveFile)
    }
}

class MenuSavesPause : MenuSaves() {
    override val titleLogo: Texture = resourceManager.getTextureAsset(TITLE_LOGO_W)
    override val fontColor: Color = Color.WHITE
    override val selectColor: Color = Constant.DARK_RED
    override val backScreen: ScreenType = ScreenType.MENU_PAUSE

    override fun loadSelectedSave() {
        QuestionDialog(LOAD_MESSAGE) { fadeBeforeOpenWorldScreen(selectedSaveFile) }.show(stage)
    }
}

abstract class MenuSaves : MenuLoad() {

    private lateinit var saveFiles: List<SaveFile>
    private lateinit var saves: Array<ListRow>

    private lateinit var topTable: Table
    private lateinit var listItems: GdxList<ListRow>
    private lateinit var group: VerticalGroup

    private lateinit var listenerKeyVertical: ListenerKeyVertical
    private lateinit var listenerKeyHorizontal: ListenerKeyHorizontal

    private var selectedListIndex = 0
    private var isLoaded = false

    protected val selectedSaveFile: SaveFile get() = saveFiles[selectedListIndex]

    protected abstract fun loadSelectedSave()

    override fun setupScreen() {
        isLoaded = false
        saves = profileManager.getVisualLoadingArrayForSaves().toListRows()

        createTables()
        applyHorizontalListener()

        stage.addActor(topTable)
        stage.addActor(table)
        stage.keyboardFocus = group
        stage.addAction(Actions.alpha(1f))

        super.selectedMenuIndex = 0
        setCurrentTextButtonToSelected()

        thread { loadSaves() }
    }

    override fun updateSelectedIndices() {
        if (isLoaded) {
            selectedListIndex = listItems.selectedIndex
            listenerKeyVertical.updateSelectedIndex(selectedListIndex)
        }
        listenerKeyHorizontal.updateSelectedIndex(selectedMenuIndex)
    }

    private fun loadSaves() {
        val loadedSaveFiles: List<SaveFile> = profileManager.getSaveFilesOfSelectedProfile()
        val loadedSaves: Array<ListRow> = profileManager.getVisualSaveArray(loadedSaveFiles).toListRows()
        Gdx.app.postRunnable {
            saveFiles = loadedSaveFiles
            saves = loadedSaves
            listItems.setItems(saves)
            listItems.selectedIndex = 0
            placeButtonsBelowList()
            applyAllListeners()
            isLoaded = true
        }
    }

    private fun selectMenuItem() {
        when (selectedMenuIndex) {
            0 -> processLoadButton()
            1 -> processBackButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processLoadButton() {
        if (!isLoaded || saves[selectedListIndex].text.contains(INVALID_PROFILE_VIEW)) {
            errorSound()
        } else {
            loadSelectedSave()
        }
    }

    private fun createTables() {
        // styles
        val titleStyle = LabelStyle(menuFont, fontColor)
        val buttonStyle = TextButtonStyle()
        buttonStyle.font = menuFont
        buttonStyle.fontColor = fontColor
        val listStyle = ListStyle()
        listStyle.font = menuFont
        listStyle.fontColorSelected = selectColor
        listStyle.fontColorUnselected = fontColor
        listStyle.background = Color.CLEAR.toDrawable()
        listStyle.selection = Color.CLEAR.toDrawable()

        // actors
        val titleLabel = Label(TITLE_LABEL, titleStyle)
        val loadButton = TextButton(MENU_ITEM_LOAD, TextButtonStyle(buttonStyle))
        val backButton = TextButton(MENU_ITEM_BACK, TextButtonStyle(buttonStyle))

        listItems = GdxList(listStyle)
        listItems.setItems(saves)
        listItems.alignment = Align.center
        group = VerticalGroup()
        group.addActor(listItems)

        // tables
        topTable = Table()
        topTable.setFillParent(true)
        topTable.add(titleLabel).center().spaceBottom(TITLE_SPACE_BOTTOM).row()
        topTable.add(group).center()
        topTable.x = MENU_X
        val logo: Image = stage.root.findActor(TITLE_LOGO_NAME)
        topTable.top().padTop((logo.height * logo.scaleY) + LOGO_PAD + PAD_TOP)

        // bottom table
        table = Table()
        table.setFillParent(true)
        table.add(loadButton).spaceRight(BUTTON_SPACE_RIGHT)
        table.add(backButton)
        table.top().right().padRight(((logo.width * logo.scaleX) / 2f) - (table.prefWidth / 2f) + LOGO_PAD)
        placeButtonsBelowList()
    }

    private fun placeButtonsBelowList() {
        table.padTop(TITLE_SPACE_BOTTOM + topTable.prefHeight + BUTTON_SPACE_TOP)
        // changing the padding doesn't trigger a new layout, so after loading the buttons would stay in place.
        table.invalidate()
    }

    private fun applyHorizontalListener() {
        listenerKeyHorizontal = ListenerKeyHorizontal({ super.updateMenuIndex(it) }, NUMBER_OF_ITEMS)
        group.addListener(listenerKeyHorizontal)
    }

    private fun applyAllListeners() {
        listenerKeyVertical = ListenerKeyVertical({ listItems.setSelectedIndex(it) }, listItems.items.size)
        val listenerKeyConfirm = ListenerKeyConfirm { selectMenuItem() }
        val listenerKeyCancel = ListenerKeyCancel({ super.updateMenuIndex(it) }, { selectMenuItem() }, EXIT_INDEX)
        group.addListener(listenerKeyVertical)
        group.addListener(listenerKeyConfirm)
        group.addListener(listenerKeyCancel)
    }

}
