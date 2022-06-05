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
import nl.t64.cot.components.party.SpellsRewarder
import nl.t64.cot.components.party.XpRewarder
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.academy.AcademyScreen
import nl.t64.cot.screens.loot.RewardScreen
import nl.t64.cot.screens.loot.TradeScreen
import nl.t64.cot.screens.school.SchoolScreen
import nl.t64.cot.screens.shop.ShopScreen


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

class ConversationDialog(conversationObserver: ConversationObserver) {

    private val conversationObserver: ConversationSubject = ConversationSubject(conversationObserver)

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

    fun update(dt: Float) {
        stage.act(dt)
        stage.draw()
    }

    fun isVisible(): Boolean {
        return scrollPane.hasKeyboardFocus()
    }

    fun loadConversation(conversationId: String, entityId: String) {
        this.conversationId = conversationId
        faceId = entityId
        graph = gameData.conversations.getConversationById(conversationId)
        fillDialogForConversation()
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_START)
        populateConversationDialog(graph.currentPhraseId)
        applyListeners()
    }

    fun loadNote(noteId: String) {
        conversationId = null
        faceId = null
        graph = getNoteById(noteId)
        fillDialogForNote()
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_START)
        populateConversationDialog(graph.currentPhraseId)
        applyListeners()
    }

    fun setFaceColor(color: Color) {
        faceImage!!.color = color
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun selectAnswer() {
        val selectedAnswer = answers.selected
        if (!selectedAnswer.isMeetingCondition()) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_ERROR)
            return
        }
        val nextId = selectedAnswer.nextId
        val conversationCommand = selectedAnswer.command

        when (conversationCommand) {
            ConversationCommand.NONE -> continueConversation(nextId)
            ConversationCommand.EXIT -> endConversation(nextId)
            ConversationCommand.HERO_JOIN -> tryToAddHeroToParty(nextId)
            ConversationCommand.HERO_DISMISS -> dismissHero(nextId)
            ConversationCommand.LOAD_SHOP -> loadShop(nextId)
            ConversationCommand.LOAD_ACADEMY -> loadAcademy(nextId)
            ConversationCommand.LOAD_SCHOOL -> loadSchool(nextId)
            ConversationCommand.SAVE_GAME -> saveGame(nextId)
            ConversationCommand.HEAL_LIFE -> healLife(nextId)
            ConversationCommand.RECEIVE_XP -> receiveXp(nextId)
            ConversationCommand.RECEIVE_SPELLS -> receiveSpells(nextId)
            ConversationCommand.START_BATTLE -> startBattle(nextId)
            ConversationCommand.RELOAD_NPCS -> reloadNpcs(nextId)

            ConversationCommand.KNOW_QUEST -> knowQuest(nextId)
            ConversationCommand.ACCEPT_QUEST -> acceptQuest(nextId)
            ConversationCommand.TRADE_QUEST_ITEMS -> tradeQuestItems()
            ConversationCommand.RE_TRADE_QUEST_ITEMS -> reTradeQuestItems()
            ConversationCommand.SHOW_QUEST_ITEM -> showQuestItem(nextId)
            ConversationCommand.WEAR_QUEST_ITEM -> wearQuestItem(nextId)
            ConversationCommand.GIVE_QUEST_ITEM -> giveQuestItem(nextId)
            ConversationCommand.SAY_QUEST_THING -> sayQuestThing(nextId)

            ConversationCommand.REWARD_QUEST -> rewardQuest()

            else -> throw IllegalArgumentException("ConversationCommand '$conversationCommand' cannot be reached here.")
        }
    }

    private fun tryToAddHeroToParty(nextId: String) {
        val heroes = gameData.heroes
        val party = gameData.party
        val hero = heroes.getCertainHero(faceId!!)

        if (party.isFull) {
            continueConversation(Constant.PHRASE_ID_PARTY_FULL)
        } else {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_JOIN)
            heroes.removeHero(faceId!!)
            party.addHero(hero)
            conversationObserver.notifyHeroJoined()
            endConversationWithoutSound(nextId)
        }
    }

    private fun dismissHero(nextId: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_END)
        endConversationWithoutSound(nextId)
        conversationObserver.notifyHeroDismiss()
    }

    private fun loadShop(nextId: String) {
        endConversationBeforeLoadScreen(nextId)
        ShopScreen.load(faceId!!, conversationId!!)
    }

    private fun loadAcademy(nextId: String) {
        endConversationBeforeLoadScreen(nextId)
        AcademyScreen.load(faceId!!, conversationId!!)
    }

    private fun loadSchool(nextId: String) {
        endConversationBeforeLoadScreen(nextId)
        SchoolScreen.load(faceId!!, conversationId!!)
    }

    private fun saveGame(nextId: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_SAVE_GAME)
        endConversation(nextId)
        profileManager.saveProfile()
    }

    private fun healLife(nextId: String) {
        val price = conversationId!!.substringAfterLast("-").toInt()
        if (price > 0) {
            if (gameData.inventory.hasEnoughOfItem("gold", price)) {
                pay(price)
            } else {
                continueConversation(Constant.PHRASE_ID_INN_NEGATIVE)
                return
            }
        } else {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_RESTORE)
        }
        delayInputListeners()
        gameData.party.recoverFullHp()
        brokerManager.mapObservers.notifyFadeOut({ continueConversation(nextId) }, delay = 1f)
    }

    private fun pay(price: Int) {
        gameData.inventory.autoRemoveItem("gold", price)
        stage.addAction(Actions.sequence(
            Actions.run { audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_COINS_BUY) },
            Actions.delay(1f),
            Actions.run { audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_RESTORE) }
        ))
    }

    private fun receiveXp(nextId: String) {
        XpRewarder.receivePossibleXp(conversationId!!)
        continueConversation(nextId)
    }

    private fun receiveSpells(nextId: String) {
        SpellsRewarder.receivePossibleSpells(conversationId!!)
        continueConversation(nextId)
    }

    private fun startBattle(nextId: String) {
        endConversation(nextId)
        conversationObserver.notifyShowBattleScreen(conversationId!!)
    }

    private fun reloadNpcs(nextId: String) {
        endConversation(nextId)
        conversationObserver.notifyReloadNpcs()
    }

    private fun knowQuest(nextId: String) {
        gameData.quests.getQuestById(conversationId!!).know()
        continueConversation(nextId)
    }

    private fun acceptQuest(nextId: String) {
        gameData.quests.getQuestById(conversationId!!).accept()
        continueConversation(nextId)
    }

    private fun tradeQuestItems() {
        val quest = gameData.quests.getQuestById(conversationId!!)
        val receive = quest.possibleSetTradeItemsTaskComplete()
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_END)
        stage.addAction(Actions.sequence(Actions.run { hideWithFade() },
                                         Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
                                         Actions.run { TradeScreen.load(receive, graph) }))
    }

    private fun reTradeQuestItems() {
        val quest = gameData.quests.getQuestById(conversationId!!)
        val receive = quest.getReceiveItemsForgottenTradeItemsTask()
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_END)
        stage.addAction(Actions.sequence(Actions.run { hideWithFade() },
                                         Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
                                         Actions.run { TradeScreen.load(receive, graph) }))
    }

    private fun showQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId!!).possibleSetShowItemTaskComplete()
        endConversation(nextId)
    }

    private fun wearQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId!!).possibleSetWearItemTaskComplete()
        endConversation(nextId)
    }

    private fun giveQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId!!).possibleSetGiveItemTaskComplete()
        continueConversation(nextId)
    }

    private fun sayQuestThing(nextId: String) {
        gameData.quests.getQuestById(conversationId!!).setSayTheRightThingTaskComplete()
        endConversation(nextId)
    }

    private fun rewardQuest() {
        val quest = gameData.quests.getQuestById(conversationId!!)
        val reward = gameData.loot.getLoot(conversationId!!)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_END)
        stage.addAction(Actions.sequence(Actions.run { hideWithFade() },
                                         Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
                                         Actions.run { RewardScreen.load(reward, quest, graph) })
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun delayInputListeners() {
        stage.addAction(Actions.sequence(
            Actions.run { scrollPane.clearListeners() },
            Actions.delay(1f),
            Actions.run { applyListeners() }
        ))
    }

    private fun applyListeners() {
        scrollPane.addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.addListener(ConversationDialogListener(answers) { selectAnswer() }, false)
        ))
    }

    private fun continueConversation(nextId: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_NEXT)
        continueConversationWithoutSound(nextId)
    }

    private fun continueConversationWithoutSound(nextId: String) {
        populateConversationDialog(nextId)
    }

    private fun endConversation(nextId: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_END)
        endConversationWithoutSound(nextId)
    }

    private fun endConversationWithoutSound(nextId: String) {
        graph.currentPhraseId = nextId
        hideWithFade()
        conversationObserver.notifyExitConversation()
    }

    private fun endConversationBeforeLoadScreen(nextId: String) {
        graph.currentPhraseId = nextId
        hide()
        conversationObserver.notifyExitConversation()
    }

    private fun hideWithFade() {
        scrollPane.clearListeners()
        dialog.hide()
    }

    private fun hide() {
        scrollPane.clearListeners()
        dialog.hide(null)
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
        repositionScrollPaneBasedOnContent(choices)
    }

    private fun setDefaultSelectedChoice(choices: GdxArray<ConversationChoice>) {
        if (choices.size == 1) {
            answers.setSelectedIndex(0)
        } else {
            answers.setSelectedIndex(-1)
        }
    }

    private fun repositionScrollPaneBasedOnContent(choices: GdxArray<ConversationChoice>) {
        setScrollPaneHeight(choices)
        setScrollPanePadding()
    }

    private fun setScrollPaneHeight(choices: GdxArray<ConversationChoice>) {
        if (choices.size % 2 == 0) {
            rowWithScrollPane.height(choices.size * SCROLL_PANE_LINE_HEIGHT)
        } else {
            rowWithScrollPane.height(choices.size * SCROLL_PANE_LINE_HEIGHT + 2f)
        }
    }

    private fun setScrollPanePadding() {
        if (label.text.isBlank()) {
            rowWithScrollPane.padTop(-SCROLL_PANE_LINE_HEIGHT - SCROLL_PANE_TOP_PAD).padLeft(0f)
        } else {
            rowWithScrollPane.padTop(0f).padLeft(PAD)
        }
    }

}
