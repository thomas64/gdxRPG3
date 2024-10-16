package nl.t64.cot.screens.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllBgm
import nl.t64.cot.audio.stopSe
import nl.t64.cot.components.cutscene.CutsceneId
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.gamestate.AUTOSAVE_INDEX
import nl.t64.cot.gamestate.DEFAULT_EMPTY_PROFILE_VIEW
import nl.t64.cot.gamestate.INVALID_PROFILE_VIEW
import nl.t64.cot.toDrawable
import kotlin.concurrent.thread


private const val TITLE_LABEL = "Select profile"
private const val MENU_ITEM_START = "Start"
private const val MENU_ITEM_DELETE = "Delete"
private const val MENU_ITEM_BACK = "Back"

private val DELETE_MESSAGE = """
    This save file will be removed. Any
    autosave file will ALSO be removed.
    Are you sure?""".trimIndent()
private val DELETE_MESSAGE_AUTOSAVE = """
    This save file will be removed.
    Are you sure?""".trimIndent()

private const val MENU_X = 604f
private const val TITLE_SPACE_BOTTOM = 45f
private const val BUTTON_SPACE_TOP = 10f
private const val BUTTON_SPACE_RIGHT = 20f

private const val NUMBER_OF_ITEMS = 3
private const val DELETE_INDEX = 1
private const val EXIT_INDEX = 2

class MenuLoadMain : MenuScreen() {

    override val titleLogo: Texture = resourceManager.getTextureAsset(TITLE_LOGO_B)
    override val fontColor: Color = Color.BLACK
    override val selectColor: Color = Constant.LIGHT_RED
    override val backScreen: ScreenType = ScreenType.MENU_MAIN

    private lateinit var profiles: Array<String>

    private lateinit var topTable: Table
    private lateinit var listItems: List<String>
    private lateinit var group: VerticalGroup

    private lateinit var listenerKeyVertical: ListenerKeyVertical
    private lateinit var listenerKeyHorizontal: ListenerKeyHorizontal

    private var selectedListIndex = 0
    private var isBgmFading = false
    private var isLoaded = false

    override fun setupScreen() {
        isLoaded = false
        profiles = profileManager.getVisualLoadingArray()

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

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        if (isLoaded) {
            selectedListIndex = listItems.selectedIndex
        }
        listenerKeyVertical.updateSelectedIndex(selectedListIndex)
        listenerKeyHorizontal.updateSelectedIndex(selectedMenuIndex)
        if (isBgmFading) {
            audioManager.certainFadeBgmBgs()
        }
        stage.draw()
    }

    private fun selectMenuItem() {
        when (selectedMenuIndex) {
            0 -> processLoadButton()
            1 -> processDeleteButton()
            2 -> processBackButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processLoadButton() {
        if (!isLoaded || profiles[selectedListIndex].contains(INVALID_PROFILE_VIEW)) {
            errorSound()
        } else if (profileManager.doesProfileExist(selectedListIndex)) {
            fadeBeforeOpenWorldScreen()
        } else if (selectedListIndex == AUTOSAVE_INDEX) {
            errorSound()
        } else {
            newGame()
        }
    }

    private fun processDeleteButton() {
        if (isLoaded && profileManager.doesProfileExist(selectedListIndex)) {
            val deleteMessage = if (selectedListIndex == AUTOSAVE_INDEX) DELETE_MESSAGE_AUTOSAVE else DELETE_MESSAGE
            DialogQuestion({ deleteSaveFile() }, deleteMessage).show(stage)
        } else {
            errorSound()
        }
    }

    private fun newGame() {
        profileManager.selectNewProfile(selectedListIndex)
        processButton(ScreenType.MENU_NEW)
    }

    private fun errorSound() {
        stopSe(AudioEvent.SE_MENU_CONFIRM)
        playSe(AudioEvent.SE_MENU_ERROR)
    }

    private fun fadeBeforeOpenWorldScreen() {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        stage.addAction(Actions.sequence(Actions.run { isBgmFading = true },
                                         Actions.fadeOut(Constant.FADE_DURATION),
                                         Actions.run { isBgmFading = false },
                                         Actions.run { stopAllBgm() },
                                         Actions.run { openWorldScreen() }))
    }

    private fun openWorldScreen() {
        mapManager.disposeOldMaps()
        screenManager.getScreen(ScreenType.WORLD) // just load the constructor.
        profileManager.loadProfileFromMain(selectedListIndex)
        if (gameData.cutscenes.isPlayed(CutsceneId.SCENE_INTRO)) {
            screenManager.setScreen(ScreenType.WORLD)
        } else {
            gameData.cutscenes.setPlayed(CutsceneId.SCENE_INTRO)
            screenManager.setScreen(ScreenType.SCENE_INTRO)
        }
    }

    private fun deleteSaveFile() {
        profileManager.removeProfile(AUTOSAVE_INDEX)
        profileManager.removeProfile(selectedListIndex)
        isLoaded = false
        profiles = profileManager.getVisualLoadingArray()
        listItems.setItems(profiles)
        updateMenuIndex(0)
        thread { loadProfiles() }
    }

    private fun loadProfiles() {
        profiles = profileManager.getVisualProfileArray()
        listItems.setItems(profiles)
        if (areAllProfilesEmpty()) selectedListIndex = 0
        listItems.selectedIndex = selectedListIndex
        isLoaded = true
    }

    private fun areAllProfilesEmpty(): Boolean {
        return profiles.all { it.contains(DEFAULT_EMPTY_PROFILE_VIEW) }
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
        val loadButton = TextButton(MENU_ITEM_START, TextButtonStyle(buttonStyle))
        val deleteButton = TextButton(MENU_ITEM_DELETE, TextButtonStyle(buttonStyle))
        val backButton = TextButton(MENU_ITEM_BACK, TextButtonStyle(buttonStyle))

        listItems = List(listStyle)
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
        table.add(loadButton).spaceRight(BUTTON_SPACE_RIGHT)
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
