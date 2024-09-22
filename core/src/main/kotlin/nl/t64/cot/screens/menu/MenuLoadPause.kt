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
import nl.t64.cot.gamestate.INVALID_PROFILE_VIEW
import nl.t64.cot.toDrawable
import kotlin.concurrent.thread


private const val TITLE_LABEL = "Load profile"
private const val MENU_ITEM_LOAD = "Load"
private const val MENU_ITEM_BACK = "Back"

private val LOAD_MESSAGE = """
    All progress after the last save will be lost.
    Are you sure you want to load this profile?""".trimIndent()

private const val MENU_X = 604f
private const val TITLE_SPACE_BOTTOM = 45f
private const val BUTTON_SPACE_TOP = 10f
private const val BUTTON_SPACE_RIGHT = 20f

private const val NUMBER_OF_ITEMS = 2
private const val EXIT_INDEX = 1

class MenuLoadPause : MenuScreen() {

    override val titleLogo: Texture = resourceManager.getTextureAsset(TITLE_LOGO_W)
    override val fontColor: Color = Color.WHITE
    override val selectColor: Color = Constant.DARK_RED
    override val backScreen: ScreenType = ScreenType.MENU_PAUSE

    private lateinit var profiles: Array<String>

    private lateinit var topTable: Table
    private lateinit var listItems: List<String>
    private lateinit var group: VerticalGroup

    private lateinit var listenerKeyVertical: ListenerKeyVertical
    private lateinit var listenerKeyHorizontal: ListenerKeyHorizontal

    private var selectedListIndex = 0
    private var isLoaded = false

    override fun setupScreen() {
        isLoaded = false
        profiles = profileManager.getVisualLoadingArrayForLoadedProfile()

        createTables()
        applyHorizontalListener()

        stage.addActor(topTable)
        stage.addActor(table)
        stage.keyboardFocus = group
        stage.addAction(Actions.alpha(1f))

        super.selectedMenuIndex = 0
        setCurrentTextButtonToSelected()

        thread {
            loadProfiles()
            applyAllListeners()
            isLoaded = true
        }
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        if (isLoaded) {
            selectedListIndex = listItems.selectedIndex
            listenerKeyVertical.updateSelectedIndex(selectedListIndex)
        }
        listenerKeyHorizontal.updateSelectedIndex(selectedMenuIndex)
        stage.draw()
    }

    private fun selectMenuItem() {
        when (selectedMenuIndex) {
            0 -> processLoadButton()
            1 -> processBackButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processLoadButton() {
        if (!isLoaded || profiles[selectedListIndex].contains(INVALID_PROFILE_VIEW)) {
            errorSound()
        } else {
            DialogQuestion({ fadeBeforeOpenWorldScreen() }, LOAD_MESSAGE).show(stage)
        }
    }

    private fun errorSound() {
        stopSe(AudioEvent.SE_MENU_CONFIRM)
        playSe(AudioEvent.SE_MENU_ERROR)
    }

    private fun fadeBeforeOpenWorldScreen() {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        stage.addAction(Actions.sequence(Actions.fadeOut(Constant.FADE_DURATION),
                                         Actions.run { stopAllBgm() },
                                         Actions.run { openWorldScreen() }))
    }

    private fun openWorldScreen() {
        mapManager.disposeOldMaps()
        screenManager.getScreen(ScreenType.WORLD) // just load the constructor.
        profileManager.loadProfileFromPause(selectedListIndex)
        if (gameData.cutscenes.isPlayed(CutsceneId.SCENE_INTRO)) {
            screenManager.setScreen(ScreenType.WORLD)
        } else {
            gameData.cutscenes.setPlayed(CutsceneId.SCENE_INTRO)
            screenManager.setScreen(ScreenType.SCENE_INTRO)
        }
    }

    private fun loadProfiles() {
        profiles = profileManager.getVisualProfileArrayForLoadedProfile()
        listItems.setItems(profiles)
        listItems.selectedIndex = 0
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
        table.add(backButton)
        table.top().padTop(TITLE_SPACE_BOTTOM + topTable.prefHeight + BUTTON_SPACE_TOP)
            .right().padRight(((logo.width * logo.scaleX) / 2f) - (table.prefWidth / 2f) + LOGO_PAD)
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
