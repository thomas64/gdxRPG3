package nl.t64.cot.screens.menu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.cutscene.CutsceneId
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


private const val PROFILE_LABEL = "Enter profile name:"
private const val PROFILE_INPUT_LENGTH = 8 + 1

private const val MENU_ITEM_START = "Start"
private const val MENU_ITEM_BACK = "Back"

private const val PROFILE_INPUT_WIDTH = 280f
private const val PROFILE_INPUT_HEIGHT = 50f
private const val BUTTON_SPACE_RIGHT = 50f
private const val SPACE_BOTTOM = 10f

private const val NUMBER_OF_ITEMS = 2
private const val EXIT_INDEX = 1

class MenuNew : MenuScreen() {

    private lateinit var profileText: TextField
    private lateinit var listenerKeyInputField: ListenerKeyInputField
    private lateinit var listenerKeyHorizontal: ListenerKeyHorizontal
    private lateinit var finalProfileName: String
    private lateinit var profileName: StringBuilder
    private var isBgmFading = false

    override fun setupScreen() {
        setFontColor()
        table = createTable()

        applyListeners()
        stage.addActor(table)
        stage.keyboardFocus = table
        stage.addAction(Actions.alpha(1f))

        finalProfileName = ""
        profileName = StringBuilder("_")
        updateInput(profileName)

        super.selectedMenuIndex = 0
        setCurrentTextButtonToSelected()
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        listenerKeyInputField.updateInputField(profileName)
        listenerKeyHorizontal.updateSelectedIndex(selectedMenuIndex)
        if (isBgmFading) {
            audioManager.fadeBgmBgs()
        }
        stage.draw()
    }

    private fun updateInput(newInput: StringBuilder) {
        profileName = newInput
        profileText.text = profileName.toString()
    }

    private fun selectMenuItem() {
        when (selectedMenuIndex) {
            0 -> processStartButton()
            1 -> processBackButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processStartButton() {
        finalProfileName = profileName.substring(0, profileName.length - 1)
        fadeBeforeCreateNewGame()
    }

    private fun fadeBeforeCreateNewGame() {
        stage.addAction(Actions.sequence(Actions.run { table.clearListeners() },
                                         Actions.run { isBgmFading = true },
                                         Actions.fadeOut(Constant.FADE_DURATION),
                                         Actions.run { isBgmFading = false },
                                         Actions.run { createNewGame() }))
    }

    private fun createNewGame() {
        screenManager.getScreen(ScreenType.WORLD) // just load the constructor.
        profileManager.createNewProfile(finalProfileName)
        gameData.cutscenes.setPlayed(CutsceneId.SCENE_INTRO)
        screenManager.setScreen(ScreenType.SCENE_INTRO)
    }

    override fun setAllTextButtonsToDefault() {
        val lowerTable = table.getChild(1) as Table // two tables inside the table, get the second.
        lowerTable.children.forEach { (it as TextButton).style.fontColor = fontColor }
    }

    override fun setCurrentTextButtonToSelected() {
        val lowerTable = table.getChild(1) as Table // two tables inside the table, get the second.
        (lowerTable.getChild(selectedMenuIndex) as TextButton).style.fontColor = Constant.DARK_RED
    }

    private fun createTable(): Table {
//        Skin uiskin = new Skin();
//        uiskin.add("default-font", menuFont, BitmapFont.class);
//        TextureAtlas atlas = new TextureAtlas(UISKIN_ATLAS);
//        uiskin.addRegions(atlas);
//        uiskin.load(Gdx.files.internal(UISKIN_JSON));

        // styles
        val menuStyle = LabelStyle(menuFont, fontColor)
        val buttonStyle = TextButtonStyle()
        buttonStyle.font = menuFont
        buttonStyle.fontColor = fontColor

        // actors
        val profileLabel = Label(PROFILE_LABEL, menuStyle)
        profileText = createProfileText()
        val startButton = TextButton(MENU_ITEM_START, TextButtonStyle(buttonStyle))
        val backButton = TextButton(MENU_ITEM_BACK, TextButtonStyle(buttonStyle))

        // table
        val newTable = Table()
        newTable.setFillParent(true)

        val upperTable = Table()
        upperTable.add(profileLabel).spaceBottom(SPACE_BOTTOM).row()
        upperTable.add(profileText).size(PROFILE_INPUT_WIDTH, PROFILE_INPUT_HEIGHT)

        val lowerTable = Table()
        lowerTable.add(startButton).spaceRight(BUTTON_SPACE_RIGHT)
        lowerTable.add(backButton)

        newTable.add(upperTable).spaceBottom(SPACE_BOTTOM).row()
        newTable.add(lowerTable)
        val logo = stage.actors.peek()
        newTable
            .top().padTop((logo.height * logo.scaleY) + LOGO_PAD + PAD_TOP)
            .right().padRight(((logo.width * logo.scaleX) / 2f) - (newTable.prefWidth / 2f) + (LOGO_PAD / 2f))
        return newTable
    }

    private fun createProfileText(): TextField {
        val textFieldStyle = TextFieldStyle().apply {
            font = menuFont
            fontColor = Color.BLACK
            background = Utils.createFullBorder()
        }
        return TextField("", textFieldStyle).apply {
            isDisabled = true
            maxLength = PROFILE_INPUT_LENGTH
            alignment = Align.center
        }
    }

    private fun applyListeners() {
        listenerKeyInputField = ListenerKeyInputField({ this.updateInput(it) }, PROFILE_INPUT_LENGTH)
        listenerKeyHorizontal = ListenerKeyHorizontal({ super.updateMenuIndex(it) }, NUMBER_OF_ITEMS)
        val listenerKeyConfirm = ListenerKeyConfirm { selectMenuItem() }
        val listenerKeyCancel = ListenerKeyCancel({ super.updateMenuIndex(it) }, { selectMenuItem() }, EXIT_INDEX)
        table.addListener(listenerKeyInputField)
        table.addListener(listenerKeyHorizontal)
        table.addListener(listenerKeyConfirm)
        table.addListener(listenerKeyCancel)
    }

}
