package nl.t64.cot.screens.world.conversation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.conversation.ConversationChoice
import nl.t64.cot.components.conversation.ConversationCommand
import nl.t64.cot.components.conversation.ConversationGraph
import nl.t64.cot.components.conversation.NoteDatabase.getNoteById
import nl.t64.cot.components.party.SpellsRewarder
import nl.t64.cot.components.party.XpRewarder
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.academy.AcademyScreen
import nl.t64.cot.screens.loot.ReceiveScreen
import nl.t64.cot.screens.loot.RewardScreen
import nl.t64.cot.screens.loot.TradeScreen
import nl.t64.cot.screens.school.SchoolScreen
import nl.t64.cot.screens.shop.ShopScreen
import nl.t64.cot.toDrawable
import kotlin.concurrent.thread


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
    private val font: BitmapFont = createFont()
    private val label: Label = createLabel()
    private val answers: List<ConversationChoice> = List(createAnswersStyle())
    private val scrollPane: ScrollPane = createScrollPane()
    private val dialog: Dialog = createDialog()

    private lateinit var conversationId: String
    private lateinit var faceId: String
    private lateinit var faceImage: Image
    private lateinit var rowWithScrollPane: Cell<ScrollPane>
    private lateinit var graph: ConversationGraph

    fun dispose() {
        stage.dispose()
        font.disposeSafely()
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

    fun loadConversation(newConversationId: String, entityId: String) {
        conversationId = newConversationId
        faceId = entityId
        graph = gameData.conversations.getConversationById(conversationId)
        fillDialogForConversation()
        playSe(AudioEvent.SE_CONVERSATION_START)
        populateConversationDialog(graph.currentPhraseId)
        applyListeners()
    }

    fun loadNote(noteId: String) {
        graph = getNoteById(noteId)
        fillDialogForNote()
        playSe(AudioEvent.SE_CONVERSATION_START)
        populateConversationDialog(graph.currentPhraseId)
        applyListeners()
    }

    fun setFaceColor(color: Color) {
        faceImage.color = color
    }

    private fun fillDialogForConversation() {
        label.setAlignment(Align.left)
        val mainTable = Table()
        mainTable.left()
        faceImage = Utils.getFaceImage(faceId)
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
            playSe(AudioEvent.SE_MENU_ERROR)
            return
        }
        val nextId = selectedAnswer.nextId
        when (val conversationCommand = selectedAnswer.command) {
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
            ConversationCommand.RECEIVE_ITEMS -> receiveItems()
            ConversationCommand.START_BATTLE -> startBattle(nextId)
            ConversationCommand.RELOAD_NPCS -> reloadNpcs(nextId)

            ConversationCommand.KNOW_QUEST -> knowQuest(nextId)
            ConversationCommand.ACCEPT_QUEST -> acceptQuest(nextId)
            ConversationCommand.TRADE_QUEST_ITEMS -> tradeQuestItems()
            ConversationCommand.SHOW_QUEST_ITEM -> showQuestItem(nextId)
            ConversationCommand.WEAR_QUEST_ITEM -> wearQuestItem(nextId)
            ConversationCommand.GIVE_QUEST_ITEM -> giveQuestItem(nextId)
            ConversationCommand.SAY_QUEST_THING -> sayQuestThing(nextId)
            ConversationCommand.RECEIVE_QUEST_ITEM_TO_DELIVER -> receiveQuestItemToDeliver()
            ConversationCommand.DELIVER_QUEST_ITEM -> deliverQuestItem(nextId)
            ConversationCommand.DELIVER_QUEST_ITEM_ALTERNATE -> deliverQuestItemAlternate(nextId)
            ConversationCommand.DELIVER_QUEST_MESSAGE -> deliverQuestMessage(nextId)

            ConversationCommand.REWARD_QUEST -> rewardQuest()

            else -> throw IllegalArgumentException("ConversationCommand '$conversationCommand' cannot be reached here.")
        }
    }

    private fun tryToAddHeroToParty(nextId: String) {
        if (gameData.party.isFull) {
            continueConversation(Constant.PHRASE_ID_PARTY_FULL)
        } else {
            addHeroToParty(nextId)
        }
    }

    private fun addHeroToParty(nextId: String) {
        playSe(AudioEvent.SE_JOIN)
        val newHero = gameData.heroes.getCertainHero(faceId)
        gameData.heroes.removeHero(faceId)
        gameData.party.addHero(newHero)
        conversationObserver.notifyHeroJoined()
        endConversationWithoutSound(nextId)
    }

    private fun dismissHero(nextId: String) {
        endConversation(nextId)
        conversationObserver.notifyHeroDismiss()
    }

    private fun loadShop(nextId: String) {
        endConversationBeforeLoadScreen(nextId)
        ShopScreen.load(faceId, conversationId)
    }

    private fun loadAcademy(nextId: String) {
        endConversationBeforeLoadScreen(nextId)
        AcademyScreen.load(faceId, conversationId)
    }

    private fun loadSchool(nextId: String) {
        endConversationBeforeLoadScreen(nextId)
        SchoolScreen.load(faceId, conversationId)
    }

    private fun saveGame(nextId: String) {
        playSe(AudioEvent.SE_SAVE_GAME)
        endConversation(nextId)
        thread { profileManager.saveProfile() }
    }

    private fun healLife(nextId: String) {
        val price = conversationId.substringAfterLast("-").toInt()
        if (price > 0) {
            if (gameData.inventory.hasEnoughOfItem("gold", price)) {
                pay(price)
            } else {
                continueConversation(Constant.PHRASE_ID_INN_NEGATIVE)
                return
            }
        } else {
            playSe(AudioEvent.SE_RESTORE)
        }
        delayInputListeners()
        Utils.runWithDelay(Constant.FADE_DURATION) {
            gameData.clock.takeHour()
            gameData.party.recoverFullHp()
        }
        screenManager.getWorldScreen().fadeOut({ continueConversation(nextId) }, duration = 1f)
    }

    private fun pay(price: Int) {
        gameData.inventory.autoRemoveItem("gold", price)
        stage.addAction(Actions.sequence(
            Actions.run { playSe(AudioEvent.SE_COINS_BUY) },
            Actions.delay(1f),
            Actions.run { playSe(AudioEvent.SE_RESTORE) }
        ))
    }

    private fun receiveXp(nextId: String) {
        XpRewarder.receivePossibleXp(conversationId)
        continueConversation(nextId)
    }

    private fun receiveSpells(nextId: String) {
        SpellsRewarder.receivePossibleSpells(conversationId)
        continueConversation(nextId)
    }

    private fun receiveItems() {
        val quest = gameData.quests.getQuestById(conversationId)
        val receive = gameData.loot.getLoot(conversationId)
        endConversationAndLoad { ReceiveScreen.load(receive, quest, graph) }
    }

    private fun startBattle(nextId: String) {
        endConversation(nextId)
        conversationObserver.notifyShowBattleScreen(conversationId)
    }

    private fun reloadNpcs(nextId: String) {
        endConversation(nextId)
        conversationObserver.notifyReloadNpcs()
    }

    private fun knowQuest(nextId: String) {
        gameData.quests.getQuestById(conversationId).know()
        continueConversation(nextId)
    }

    private fun acceptQuest(nextId: String) {
        gameData.quests.getQuestById(conversationId).accept()
        continueConversation(nextId)
    }

    private fun tradeQuestItems() {
        val receive = ConversationSpoilLoader.getLoot(conversationId) { possibleSetTradeItemsTaskComplete() }
        endConversationAndLoad { TradeScreen.load(receive, graph) }
    }

    private fun showQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId).possibleSetShowItemTaskComplete()
        endConversation(nextId)
    }

    private fun wearQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId).possibleSetWearItemTaskComplete()
        endConversation(nextId)
    }

    private fun giveQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId).possibleSetGiveItemTaskComplete()
        continueConversation(nextId)
    }

    private fun sayQuestThing(nextId: String) {
        val quest = gameData.quests.getQuestById(conversationId)
        val receive = ConversationSpoilLoader.getLoot(conversationId) { setSayTheRightThingTaskCompleteAndReceivePossibleTarget() }
        if (receive.isTaken()) {
            endConversation(nextId)
        } else {
            endConversationAndLoad { ReceiveScreen.load(receive, quest, graph) }
        }
    }

    private fun receiveQuestItemToDeliver() {
        val quest = gameData.quests.getQuestById(conversationId)
        val receive = ConversationSpoilLoader.getLoot(conversationId) { receiveItemsToDeliver() }
        endConversationAndLoad { ReceiveScreen.load(receive, quest, graph) }
    }

    private fun deliverQuestItem(nextId: String) {
        gameData.quests.updateDeliverItem(conversationId)
        continueConversation(nextId)
    }

    private fun deliverQuestItemAlternate(nextId: String) {
        gameData.quests.updateDeliverItemAlternate(conversationId)
        continueConversation(nextId)
    }

    private fun deliverQuestMessage(nextId: String) {
        gameData.quests.updateDeliverMessage(conversationId)
        continueConversation(nextId)
    }

    private fun rewardQuest() {
        val quest = gameData.quests.getQuestById(conversationId)
        val reward = gameData.loot.getLoot(conversationId)
        endConversationAndLoad { RewardScreen.load(reward, quest, graph) }
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
        playSe(AudioEvent.SE_CONVERSATION_NEXT)
        continueConversationWithoutSound(nextId)
    }

    private fun continueConversationWithoutSound(nextId: String) {
        populateConversationDialog(nextId)
    }

    private fun endConversation(nextId: String) {
        playSe(AudioEvent.SE_CONVERSATION_END)
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

    private fun endConversationAndLoad(lootScreen: () -> Unit) {
        playSe(AudioEvent.SE_CONVERSATION_END)
        stage.addAction(Actions.sequence(Actions.run { hideWithFade() },
                                         Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
                                         Actions.run { lootScreen.invoke() }))
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
        setStyleBasedOnContent(choices)
        repositionScrollPaneBasedOnContent(choices)
    }

    private fun setDefaultSelectedChoice(choices: GdxArray<ConversationChoice>) {
        if (choices.size == 1) {
            answers.selectedIndex = 0
        } else {
            answers.selectedIndex = -1
        }
    }

    private fun setStyleBasedOnContent(choices: GdxArray<ConversationChoice>) {
        val drawable = if (choices[0].isDefault()) {
            Color.CLEAR.toDrawable()
        } else {
            Utils.createFullBorder()
        }.apply {
            topHeight = SCROLL_PANE_LINE_PAD
            bottomHeight = SCROLL_PANE_LINE_PAD
        }
        answers.style = createAnswersStyle(drawable)
    }

    private fun repositionScrollPaneBasedOnContent(choices: GdxArray<ConversationChoice>) {
        setScrollPaneHeight(choices)
        setScrollPanePadding()
    }

    private fun setScrollPaneHeight(choices: GdxArray<ConversationChoice>) {
        val newHeight = if (choices.size % 2 == 0) {
            choices.size * SCROLL_PANE_LINE_HEIGHT
        } else {
            choices.size * SCROLL_PANE_LINE_HEIGHT + 2f
        }
        rowWithScrollPane.height(newHeight)
    }

    private fun setScrollPanePadding() {
        if (label.text.isBlank()) {
            rowWithScrollPane.padTop(-SCROLL_PANE_LINE_HEIGHT - SCROLL_PANE_TOP_PAD).padLeft(-(PAD / 2f))
        } else {
            rowWithScrollPane.padTop(0f).padLeft(-(PAD / 2f))
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun createFont(): BitmapFont {
        return resourceManager.getTrueTypeAsset(FONT, FONT_SIZE)
            .apply { data.setLineHeight(LINE_HEIGHT) }
    }

    private fun createLabel(): Label {
        return Label("No Conversation", LabelStyle(font, Color.BLACK))
            .apply { wrap = true }
    }

    private fun createAnswersStyle(drawable: Drawable = BaseDrawable()): ListStyle {
        return ListStyle(font, Constant.DARK_RED, Color.BLACK, drawable).apply {
            selection.leftWidth = PAD
            selection.rightWidth = PAD
        }
    }

    private fun createScrollPane(): ScrollPane {
        return ScrollPane(answers).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            setScrollingDisabled(true, true)
            setForceScroll(false, false)
            setScrollBarPositions(false, false)
        }
    }

    private fun createDialog(): Dialog {
        return Utils.createParchmentDialog(font).apply {
            background.minWidth = DIALOG_WIDTH
            background.minHeight = DIALOG_HEIGHT
            setPosition(Gdx.graphics.width / 2f - DIALOG_WIDTH / 2f, 0f)
        }
    }

}
