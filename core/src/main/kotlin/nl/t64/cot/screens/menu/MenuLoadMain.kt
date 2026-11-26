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
import nl.t64.cot.gamestate.DEFAULT_EMPTY_PROFILE_VIEW
import nl.t64.cot.gamestate.INVALID_PROFILE_VIEW
import nl.t64.cot.gamestate.SaveFile
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.toDrawable
import kotlin.concurrent.thread
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


private const val TITLE_LABEL = "Select profile"
private const val MENU_ITEM_START = "Start"
private const val MENU_ITEM_DELETE = "Delete"
private const val MENU_ITEM_BACK = "Back"

private val DELETE_MESSAGE = """
    This profile and its autosaves will be removed.
    Are you sure?""".trimIndent()

private const val MENU_X = 604f
private const val TITLE_SPACE_BOTTOM = 45f
private const val BUTTON_SPACE_TOP = 10f
private const val BUTTON_SPACE_RIGHT = 20f

private const val NUMBER_OF_ITEMS = 3
private const val DELETE_INDEX = 1
private const val EXIT_INDEX = 2

class MenuLoadMain : MenuLoad() {

    override val titleLogo: Texture = resourceManager.getTextureAsset(TITLE_LOGO_B)
    override val fontColor: Color = Color.BLACK
    override val selectColor: Color = Constant.LIGHT_RED
    override val backScreen: ScreenType = ScreenType.MENU_MAIN

    private lateinit var profiles: Array<ListRow>

    private lateinit var topTable: Table
    private lateinit var listItems: GdxList<ListRow>
    private lateinit var group: VerticalGroup

    private lateinit var listenerKeyVertical: ListenerKeyVertical
    private lateinit var listenerKeyHorizontal: ListenerKeyHorizontal

    private var selectedListIndex = 0
    private var isLoaded = false

    override fun setupScreen() {
        isLoaded = false
        profiles = profileManager.getVisualLoadingArrayForProfiles().toListRows()

        createTables()
        createSomeListeners()

        stage.addActor(topTable)
        stage.addActor(table)
        stage.keyboardFocus = group
        stage.addAction(Actions.alpha(1f))

        super.selectedMenuIndex = 0
        setCurrentTextButtonToSelected()

        thread { loadProfiles() }
        applyAllListeners()
    }

    override fun updateSelectedIndices() {
        if (isLoaded) {
            selectedListIndex = listItems.selectedIndex
        }
        listenerKeyVertical.updateSelectedIndex(selectedListIndex)
        listenerKeyHorizontal.updateSelectedIndex(selectedMenuIndex)
    }

    private fun selectMenuItem() {
        when (selectedMenuIndex) {
            0 -> processStartButton()
            1 -> processDeleteButton()
            2 -> processBackButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processStartButton() {
        if (isLoaded) {
            startSelectedProfile()
        } else {
            errorSound()
        }
    }

    private fun processDeleteButton() {
        if (isLoaded && profileManager.doesProfileExist(selectedListIndex)) {
            QuestionDialog(DELETE_MESSAGE) { deleteProfile() }.show(stage)
        } else {
            errorSound()
        }
    }

    private fun startSelectedProfile() {
        profileManager.selectProfile(selectedListIndex)
        val saveFiles: List<SaveFile> = profileManager.getSaveFilesOfSelectedProfile()
        when (saveFiles.size) {
            0 -> processButton(ScreenType.MENU_NEW)
            1 -> loadOnlySave(saveFiles.single())
            else -> processButton(ScreenType.MENU_SAVES_MAIN)
        }
    }

    private fun loadOnlySave(saveFile: SaveFile) {
        if (profiles[selectedListIndex].text.contains(INVALID_PROFILE_VIEW)) {
            errorSound()
        } else {
            fadeBeforeOpenWorldScreen(saveFile)
        }
    }

    private fun deleteProfile() {
        profileManager.removeProfile(selectedListIndex)
        isLoaded = false
        profiles = profileManager.getVisualLoadingArrayForProfiles().toListRows()
        listItems.setItems(profiles)
        updateMenuIndex(0)
        thread { loadProfiles() }
    }

    private fun loadProfiles() {
        val loadedProfiles: Array<ListRow> = profileManager.getVisualProfileArray().toListRows()
        Gdx.app.postRunnable {
            profiles = loadedProfiles
            listItems.setItems(profiles)
            if (areAllProfilesEmpty()) selectedListIndex = 0
            listItems.selectedIndex = selectedListIndex
            isLoaded = true
        }
    }

    private fun areAllProfilesEmpty(): Boolean {
        return profiles.all { it.text.contains(DEFAULT_EMPTY_PROFILE_VIEW) }
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
        val startButton = TextButton(MENU_ITEM_START, TextButtonStyle(buttonStyle))
        val deleteButton = TextButton(MENU_ITEM_DELETE, TextButtonStyle(buttonStyle))
        val backButton = TextButton(MENU_ITEM_BACK, TextButtonStyle(buttonStyle))

        listItems = GdxList(listStyle)
        listItems.setItems(profiles)
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
        table.add(startButton).spaceRight(BUTTON_SPACE_RIGHT)
        table.add(deleteButton).spaceRight(BUTTON_SPACE_RIGHT)
        table.add(backButton)
        table.top().padTop(TITLE_SPACE_BOTTOM + topTable.prefHeight + BUTTON_SPACE_TOP)
            .right().padRight(((logo.width * logo.scaleX) / 2f) - (table.prefWidth / 2f) + LOGO_PAD)
    }

    private fun createSomeListeners() {
        listenerKeyVertical = ListenerKeyVertical({ listItems.setSelectedIndex(it) }, listItems.items.size)
        listenerKeyHorizontal = ListenerKeyHorizontal({ super.updateMenuIndex(it) }, NUMBER_OF_ITEMS)
    }

    private fun applyAllListeners() {
        val listenerKeyConfirm = ListenerKeyConfirm { selectMenuItem() }
        val listenerKeyDelete = ListenerKeyDelete({ super.updateMenuIndex(it) }, { selectMenuItem() }, DELETE_INDEX)
        val listenerKeyCancel = ListenerKeyCancel({ super.updateMenuIndex(it) }, { selectMenuItem() }, EXIT_INDEX)
        group.addListener(listenerKeyVertical)
        group.addListener(listenerKeyHorizontal)
        group.addListener(listenerKeyConfirm)
        group.addListener(listenerKeyDelete)
        group.addListener(listenerKeyCancel)
    }

}
