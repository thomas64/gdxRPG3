package nl.t64.cot.screens.world.conversation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.conversation.ConversationChoice
import nl.t64.cot.components.conversation.ConversationCommand
import nl.t64.cot.components.conversation.ConversationGraph
import nl.t64.cot.components.conversation.NoteDatabase.getNoteById
import nl.t64.cot.constants.Constant


private const val SPRITE_TRANSPARENT = "sprites/transparent.png"

private const val FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24
private const val LINE_HEIGHT = 26f

private const val SCROLL_PANE_LINE_HEIGHT = 32f
private const val SCROLL_PANE_LINE_PAD = -4f
private const val SCROLL_PANE_TOP_PAD = 10f
private const val DIALOG_WIDTH = 1200f
private const val DIALOG_HEIGHT = 300f
private const val PAD = 25f
private const val ALL_PADS = PAD * 4f + Constant.FACE_SIZE + PAD + PAD * 3f

class ConversationDialog {

    val conversationObservers: ConversationSubject = ConversationSubject()

    private val stage: Stage = Stage()
    private val font: BitmapFont = resourceManager.getTrueTypeAsset(FONT, FONT_SIZE).apply {
        data.setLineHeight(LINE_HEIGHT)
    }
    private val dialog: Dialog = createDialog()

    private lateinit var label: Label
    private lateinit var answers: List<ConversationChoice>
    private lateinit var scrollPane: ScrollPane
    private lateinit var rowWithScrollPane: Cell<ScrollPane>

    private var conversationId: String? = null
    private var faceId: String? = null
    private var faceImage: Image? = null
    private lateinit var graph: ConversationGraph

    init {
        applyListeners()
    }

    fun dispose() {
        stage.dispose()
        try {
            font.dispose()
        } catch (e: GdxRuntimeException) {
            // font is already exposed.
        }
    }

    fun show() {
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
        dialog.show(stage, Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interpolation.fade)))
        stage.keyboardFocus = scrollPane
    }

    fun hideWithFade() {
        dialog.hide()
    }

    fun hide() {
        dialog.hide(null)
    }

    fun update(dt: Float) {
        stage.act(dt)
        stage.draw()
    }

    fun loadConversation(conversationId: String, entityId: String? = null) {
        this.conversationId = conversationId
        faceId = entityId
        graph = gameData.conversations.getConversationById(conversationId)
        fillDialogForConversation()
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_START)
        populateConversationDialog(graph.currentPhraseId)
    }

    fun loadNote(noteId: String) {
        conversationId = null
        faceId = null
        graph = getNoteById(noteId)
        fillDialogForNote()
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_START)
        populateConversationDialog(graph.currentPhraseId)
    }

    private fun createDialog(): Dialog {
        label = Label("No Conversation", LabelStyle(font, Color.BLACK)).apply {
            wrap = true
        }

        val spriteTransparent = Sprite(resourceManager.getTextureAsset(SPRITE_TRANSPARENT))
        val drawable = SpriteDrawable(spriteTransparent).apply {
            topHeight = SCROLL_PANE_LINE_PAD
            bottomHeight = SCROLL_PANE_LINE_PAD
        }
        answers = List(ListStyle(font, Constant.DARK_RED, Color.BLACK, drawable))

        scrollPane = ScrollPane(answers)
        scrollPane.setOverscroll(false, false)
        scrollPane.fadeScrollBars = false
        scrollPane.setScrollingDisabled(true, true)
        scrollPane.setForceScroll(false, false)
        scrollPane.setScrollBarPositions(false, false)

        return Utils.createParchmentDialog(font).apply {
            background.minWidth = DIALOG_WIDTH
            background.minHeight = DIALOG_HEIGHT
            setPosition(Gdx.graphics.width / 2f - DIALOG_WIDTH / 2f, 0f)
        }
    }

    private fun fillDialogForConversation() {
        label.setAlignment(Align.left)
        val mainTable = Table()
        mainTable.left()
        faceImage = Utils.getFaceImage(faceId!!)
        mainTable.add<Actor>(faceImage).width(Constant.FACE_SIZE).padLeft(PAD * 4f)

        val textTable = Table()
        textTable.pad(PAD, PAD, PAD, PAD * 3f)
        textTable.add<Actor>(label).width(DIALOG_WIDTH - ALL_PADS).row()
        textTable.add().height(SCROLL_PANE_TOP_PAD).row()
        textTable.add<Actor>(scrollPane).left().padLeft(PAD)
        rowWithScrollPane = textTable.getCell(scrollPane)

        mainTable.add(textTable)
        dialog.contentTable.clear()
        dialog.contentTable.add(mainTable)
    }

    private fun fillDialogForNote() {
        label.setAlignment(Align.center)
        val textTable = Table()
        textTable.pad(PAD * 2f, PAD * 3f, PAD, PAD * 2f)
        textTable.add<Actor>(label).width(DIALOG_WIDTH - PAD * 5f).row()
        textTable.add().height(SCROLL_PANE_TOP_PAD).row()
        textTable.add<Actor>(scrollPane).left().padLeft(PAD)
        rowWithScrollPane = textTable.getCell(scrollPane)

        dialog.contentTable.clear()
        dialog.contentTable.add(textTable)
    }

    private fun applyListeners() {
        scrollPane.addListener(ConversationDialogListener(answers) { selectAnswer() })
    }

    private fun continueConversation(destinationId: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_NEXT)
        continueConversationWithoutSound(destinationId)
    }

    private fun continueConversationWithoutSound(destinationId: String) {
        populateConversationDialog(destinationId)
    }

    private fun endConversation(destinationId: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_END)
        endConversationWithoutSound(destinationId)
    }

    private fun endConversationWithoutSound(destinationId: String) {
        graph.currentPhraseId = destinationId
        conversationObservers.notifyExitConversation()
    }

    private fun populateConversationDialog(phraseId: String) {
        graph.currentPhraseId = phraseId
        populateFace()
        populatePhrase()
        populateChoices()
    }

    private fun populateFace() {
        if (graph.getCurrentFace().isNotBlank()) {
            val mainTable = dialog.contentTable.getChild(0) as Table
            val faceCell = mainTable.getCell(faceImage)
            faceImage = Utils.getFaceImage(graph.getCurrentFace())
            faceCell.setActor<Actor>(faceImage)
        }
    }

    private fun populatePhrase() {
        val text = graph.getCurrentPhrase().joinToString(System.lineSeparator())
        label.setText(text)
    }

    private fun populateChoices() {
        val choices = GdxArray(graph.getAssociatedChoices())
        answers.setItems(choices)
        setDefaultSelectedChoice(choices)
        setScrollPaneHeight(choices)
    }

    private fun setDefaultSelectedChoice(choices: GdxArray<ConversationChoice>) {
        if (choices.size == 1) {
            answers.setSelectedIndex(0)
        } else {
            answers.setSelectedIndex(-1)
        }
    }

    private fun setScrollPaneHeight(choices: GdxArray<ConversationChoice>) {
        if (choices.size % 2 == 0) {
            rowWithScrollPane.height(choices.size * SCROLL_PANE_LINE_HEIGHT)
        } else {
            rowWithScrollPane.height(choices.size * SCROLL_PANE_LINE_HEIGHT + 2f)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun selectAnswer() {
        val selectedAnswer = answers.selected
        if (!selectedAnswer.isMeetingCondition()) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_ERROR)
            return
        }
        val destinationId = selectedAnswer.destinationId
        val conversationCommand = selectedAnswer.conversationCommand

        when (conversationCommand) {
            ConversationCommand.NONE -> continueConversation(destinationId)
            ConversationCommand.EXIT_CONVERSATION -> endConversation(destinationId)
            ConversationCommand.HERO_JOIN -> tryToAddHeroToParty(destinationId)
            ConversationCommand.HERO_DISMISS -> dismissHero(destinationId)
            ConversationCommand.LOAD_SHOP -> loadShop(destinationId)
            ConversationCommand.SAVE_GAME -> saveGame(destinationId)
            ConversationCommand.HEAL_LIFE -> healLife(destinationId)
            ConversationCommand.RECEIVE_XP -> receiveXp(destinationId)
            ConversationCommand.START_BATTLE -> startBattle(destinationId)

            ConversationCommand.ACCEPT_QUEST -> acceptQuest(destinationId)
            ConversationCommand.SHOW_QUEST_ITEM -> showQuestItem(destinationId)
            ConversationCommand.WEAR_QUEST_ITEM -> wearQuestItem(destinationId)
            ConversationCommand.SAY_QUEST_THING -> sayQuestThing(destinationId)

            else -> throw IllegalArgumentException("ConversationCommand '$conversationCommand' cannot be reached here.")
        }
    }

    private fun tryToAddHeroToParty(destinationId: String) {
        val heroes = gameData.heroes
        val party = gameData.party
        val hero = heroes.getCertainHero(faceId!!)

        if (party.isFull) {
            continueConversation(Constant.PHRASE_ID_PARTY_FULL)
        } else {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_JOIN)
            heroes.removeHero(faceId!!)
            party.addHero(hero)
            conversationObservers.notifyHeroJoined()
            endConversationWithoutSound(destinationId)
        }
    }

    private fun dismissHero(destinationId: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_END)
        graph.currentPhraseId = destinationId
        conversationObservers.notifyHeroDismiss()                                   // ends conversation
    }

    private fun loadShop(destinationId: String) {
        graph.currentPhraseId = destinationId
        conversationObservers.notifyLoadShop()                                      // ends conversation
    }

    private fun saveGame(destinationId: String) {
        endConversation(destinationId)
        profileManager.saveProfile()
    }

    private fun healLife(destinationId: String) {
        val price = conversationId!!.substringAfterLast("-").toInt()
        if (price > 0) {
            if (gameData.inventory.hasEnoughOfItem("gold", price)) {
                gameData.inventory.autoRemoveItem("gold", price)
                audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_COINS_BUY)
            } else {
                continueConversation(Constant.PHRASE_ID_INN_NEGATIVE)
                return
            }
        }
        gameData.party.recoverFullHp()
        brokerManager.mapObservers.notifyFadeOut(
            { continueConversation(destinationId) }
        )
    }

    private fun receiveXp(destinationId: String) {
        val reward = gameData.loot.getLoot(conversationId!!)
        if (reward.isXpGained()) {
            continueConversation(destinationId)
        } else {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_REWARD)
            val levelUpMessage = StringBuilder()
            gameData.party.gainXp(reward.xp, levelUpMessage)
            val finalMessage = levelUpMessage.toString().trim()
            if (finalMessage.isEmpty()) {
                conversationObservers.notifyShowMessageTooltip("+ ${reward.xp} XP")
            } else {
                conversationObservers.notifyShowMessageTooltip("+ ${reward.xp} XP" + System.lineSeparator()
                                                                       + System.lineSeparator() + finalMessage)
            }
            reward.clearXp()
            continueConversationWithoutSound(destinationId)
        }
    }

    private fun startBattle(destinationId: String) {
        endConversation(destinationId)
        brokerManager.componentObservers.notifyShowBattleScreen(conversationId!!)
    }

    private fun acceptQuest(destinationId: String) {
        gameData.quests.getQuestById(conversationId!!).accept()
        endConversation(destinationId)
    }

    private fun showQuestItem(destinationId: String) {
        gameData.quests.getQuestById(conversationId!!).possibleSetShowItemTaskComplete()
        endConversation(destinationId)
    }

    private fun wearQuestItem(destinationId: String) {
        gameData.quests.getQuestById(conversationId!!).possibleSetWearItemTaskComplete()
        endConversation(destinationId)
    }

    private fun sayQuestThing(destinationId: String) {
        gameData.quests.getQuestById(conversationId!!).setSayTheRightThingTaskComplete()
        endConversation(destinationId)
    }

}
