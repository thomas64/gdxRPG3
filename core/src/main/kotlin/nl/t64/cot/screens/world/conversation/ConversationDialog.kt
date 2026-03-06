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
import com.badlogic.gdx.utils.Align
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.conversation.ConversationChoice
import nl.t64.cot.components.conversation.ConversationCommand
import nl.t64.cot.components.conversation.ConversationGraph
import nl.t64.cot.components.conversation.NoteDatabase
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.SkillsRewarder
import nl.t64.cot.components.party.SpellsRewarder
import nl.t64.cot.components.party.XpRewarder
import nl.t64.cot.components.party.abilities.ResourceType
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.screens.academy.AcademyScreen
import nl.t64.cot.screens.loot.ReceiveScreen
import nl.t64.cot.screens.loot.RewardScreen
import nl.t64.cot.screens.loot.TradeScreen
import nl.t64.cot.screens.school.SchoolScreen
import nl.t64.cot.screens.shop.ShopScreen
import nl.t64.cot.sfx.TransitionPurpose
import kotlin.concurrent.thread


private const val DIALOG_WIDTH = 1200f
private const val DIALOG_HEIGHT = 300f
private const val SCROLL_PANE_TOP_PAD = 24f
private const val ARROW_PAD_LEFT = 780f
private const val PAD = 25f
private const val LEFT_PAD = PAD * 4f
private const val RIGHT_PAD = PAD * 3f
private const val ALL_PADS = LEFT_PAD + Constant.FACE_SIZE + PAD + RIGHT_PAD
private const val NAME_LABEL_PAD_LEFT = -Constant.FACE_SIZE + 3f
private const val NAME_LABEL_PAD_BOTTOM = -18f

class ConversationDialog(conversationObserver: ConversationObserver) {

    private val conversationObserver: ConversationSubject = ConversationSubject(conversationObserver)

    private val stage: Stage = Stage()
    private val font: BitmapFont = FontProvider.inconsolata24
    private val smallFont: BitmapFont = FontProvider.default
    private val label: TypingLabel = createLabel()
    private val answers: ConversationAnswers = ConversationAnswers(font)
    private val scrollPane: ScrollPane = createScrollPane()
    private val dialog: Dialog = createDialog()

    private lateinit var conversationId: String
    private lateinit var faceId: String
    private lateinit var faceImage: Image
    private lateinit var nameLabel: Label
    private lateinit var rowWithScrollPane: Cell<ScrollPane>
    private lateinit var graph: ConversationGraph

    fun dispose() {
        stage.dispose()
        font.disposeSafely()
        smallFont.disposeSafely()
    }

    fun show() {
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
        dialog.show(stage, Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interpolation.fade)))
        stage.keyboardFocus = scrollPane
    }

    fun tryToClose() {
        if (isVisible()) hide()
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
        graph.possibleSetAlternateStartingPhraseId()
        fillDialogForConversation()
        playSe(AudioEvent.SE_CONVERSATION_START)
        populateDialog(graph.currentPhraseId)
        applyListeners()
    }

    fun loadNote(noteId: String) {
        conversationId = noteId
        faceId = ""
        graph = NoteDatabase.getNoteById(conversationId)
        fillDialogForNote()
        playSe(AudioEvent.SE_CONVERSATION_START)
        populateDialog(graph.currentPhraseId)
        applyListeners()
    }

    fun setFaceColor(color: Color) {
        faceImage.color = color
    }

    private fun fillDialogForConversation() {
        label.setAlignment(Align.left)
        val mainTable = Table()
        mainTable.left()
        mainTable.add(createFaceTable())
        mainTable.add(createTextTable())

        dialog.contentTable.clear()
        dialog.contentTable.add(mainTable)
    }

    private fun createFaceTable(): Table {
        faceImage = Utils.getFaceImage(faceId)
        if (conversationId.startsWith("bury_")) {
            faceImage.color = Color.DARK_GRAY
        }
        nameLabel = Label(graph.npcName, LabelStyle(smallFont, Color.BLACK))
        return Table().apply {
            add<Actor>(faceImage).width(Constant.FACE_SIZE).padLeft(LEFT_PAD)
            add<Actor>(nameLabel).bottom().left().padLeft(NAME_LABEL_PAD_LEFT).padBottom(NAME_LABEL_PAD_BOTTOM)
        }
    }

    private fun createTextTable(): Table {
        return Table().apply {
            pad(PAD, PAD, PAD, RIGHT_PAD)
            add<Actor>(label).width(DIALOG_WIDTH - ALL_PADS).row()
            add<Actor>(scrollPane).left().padLeft(PAD)
        }.also { rowWithScrollPane = it.getCell(scrollPane) }
    }

    private fun fillDialogForNote() {
        label.setAlignment(Align.center)
        val textTable = Table()
        textTable.pad(PAD * 2f, PAD * 3f, PAD, PAD * 2f)
        textTable.add<Actor>(label).width(DIALOG_WIDTH - PAD * 5f).row()
        textTable.add<Actor>(scrollPane).left().padLeft(PAD)
        rowWithScrollPane = textTable.getCell(scrollPane)

        dialog.contentTable.clear()
        dialog.contentTable.add(textTable)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun selectAnswer() {
        if (!label.hasEnded()) {
            label.skipToTheEnd()
            return
        }
        val selectedChoice = answers.selected
        if (!selectedChoice.isMeetingCondition()) {
            playSe(AudioEvent.SE_MENU_ERROR)
            return
        }
        answers.storeSelectedIndex()
        selectedChoice.hasBeenSelectedEarlier = true
        val nextId = selectedChoice.nextId
        when (val conversationCommand = selectedChoice.command) {
            ConversationCommand.NONE -> continueConversation(nextId)
            ConversationCommand.EXIT -> endConversation(nextId)
            ConversationCommand.HERO_JOIN -> tryToAddHeroToParty(nextId)
            ConversationCommand.HERO_DISMISS -> dismissHero(nextId)
            ConversationCommand.LOAD_SHOP -> loadShop(nextId)
            ConversationCommand.LOAD_ACADEMY -> loadAcademy(nextId)
            ConversationCommand.LOAD_SCHOOL -> loadSchool(nextId)
            ConversationCommand.AUTO_SAVE -> autoSave(nextId)
            ConversationCommand.SAVE_GAME -> saveGame(nextId)
            ConversationCommand.HEAL_LIFE_0015 -> healLife(nextId, "00:15")
            ConversationCommand.HEAL_LIFE_0060 -> healLife(nextId, "00:60")
            ConversationCommand.HEAL_LIFE_0900 -> healLife(nextId, "09:00")
            ConversationCommand.HEAL_LIFE_1200 -> healLife(nextId, "12:00")
            ConversationCommand.HEAL_LIFE_1500 -> healLife(nextId, "15:00")
            ConversationCommand.HEAL_LIFE_1800 -> healLife(nextId, "18:00")
            ConversationCommand.RECEIVE_XP -> receiveXp(nextId)
            ConversationCommand.RECEIVE_SPELLS -> receiveSpells(nextId)
            ConversationCommand.RECEIVE_SKILLS -> receiveSkills(nextId)
            ConversationCommand.RECEIVE_ITEM -> receiveItem()
            ConversationCommand.START_BATTLE -> startBattle(nextId)
            ConversationCommand.RELOAD_NPCS -> fadeAndReloadNpcs(nextId)
            ConversationCommand.FADE_TO_BLACK_17 -> fadeAndSetTime("17:01", nextId)

            ConversationCommand.KNOW_QUEST -> knowQuest(nextId)
            ConversationCommand.ACCEPT_QUEST -> acceptQuest(nextId)
            ConversationCommand.FAIL_QUEST -> failQuest(nextId)
            ConversationCommand.TRADE_QUEST_ITEMS -> tradeQuestItems()
            ConversationCommand.SHOW_QUEST_ITEM -> showQuestItem(nextId)
            ConversationCommand.WEAR_QUEST_ITEM -> wearQuestItem(nextId)
            ConversationCommand.PROVIDE_QUEST_ITEM -> provideQuestItem(nextId)
            ConversationCommand.SAY_QUEST_THING -> sayQuestThing(nextId)
            ConversationCommand.RECEIVE_ITEM_FOR_QUEST -> receiveItemForQuest()
            ConversationCommand.DELIVER_QUEST_ITEM -> deliverQuestItem(nextId)
            ConversationCommand.DELIVER_QUEST_ITEM_ALTERNATE -> deliverQuestItemAlternate(nextId)
            ConversationCommand.DELIVER_QUEST_MESSAGE -> deliverQuestMessage(nextId)

            ConversationCommand.REWARD_QUEST -> rewardQuest()
            ConversationCommand.FINISH_ALL_TASKS_AND_REWARD_QUEST_LINKED -> finishAllAndRewardLinkedQuest()

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

    private fun autoSave(nextId: String) {
        endConversation(nextId)
        thread { profileManager.autoSave() }
    }

    private fun saveGame(nextId: String) {
        playSe(AudioEvent.SE_SAVE_GAME)
        endConversation(nextId)
        thread { profileManager.saveProfile() }
    }

    private fun healLife(nextId: String, time: String) {
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
            when (time) {
                "00:15" -> gameData.clock.takeQuarterHour()
                "00:60" -> gameData.clock.takeHour()
                else -> gameData.clock.setTimeOfDay(time)
            }
            gameData.party.fullRecover()
        }
        conversationObserver.notifyFade(duration = 1f, actionAfterFade = { continueConversation(nextId) })
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

    private fun receiveSkills(nextId: String) {
        SkillsRewarder.receivePossibleSkills(conversationId)
        continueConversation(nextId)
    }

    private fun receiveItem() {
        val receive: Loot = gameData.loot.getLoot(conversationId)
        if (receive.isTaken()) {
            endConversation(Constant.PHRASE_ID_LOOT_TAKEN)
        } else {
            endConversationAndLoad { ReceiveScreen.load(receive, null, graph) }
        }
    }

    private fun startBattle(nextId: String) {
        endConversation(nextId)
        conversationObserver.notifyShowBattleScreen(conversationId)
    }

    private fun fadeAndReloadNpcs(nextId: String) {
        // in dialog state, scheduled npc's won't update. that's why we first reload them with a fade to black.
        // and then, when the conversation ends, they disappear, because they are updated, but then it's already black.
        conversationObserver.notifyJustFadeAndReloadNpcs()
        endConversation(nextId)
    }

    private fun fadeAndSetTime(time: String, nextId: String) {
        endConversation(nextId)
        conversationObserver.notifyFade(duration = 2f,
                                        transitionPurpose = TransitionPurpose.UPDATE,
                                        actionDuringFade = { gameData.clock.setTimeOfDay(time) })
    }

    private fun knowQuest(nextId: String) {
        gameData.quests.getQuestById(conversationId).know()
        continueConversation(nextId)
    }

    private fun acceptQuest(nextId: String) {
        gameData.quests.getQuestById(conversationId).accept()
        continueConversation(nextId)
    }

    private fun failQuest(nextId: String) {
        gameData.quests.getQuestById(conversationId).fail()
        continueConversation(nextId)
    }

    private fun tradeQuestItems() {
        val receive = ConversationSpoilLoader.getLoot(conversationId, QuestGraph::possibleSetTradeItemsTaskComplete)
        endConversationAndLoad { TradeScreen.load(receive, graph) }
    }

    private fun showQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId).possibleSetShowItemTaskComplete()
        continueConversation(nextId)
    }

    private fun wearQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId).possibleSetWearItemTaskComplete()
        endConversation(nextId)
    }

    private fun provideQuestItem(nextId: String) {
        gameData.quests.getQuestById(conversationId).possibleSetProvideItemTaskComplete()
        continueConversation(nextId)
    }

    private fun sayQuestThing(nextId: String) {
        val quest = gameData.quests.getQuestById(conversationId)
        val possibleReceive = ConversationSpoilLoader.getLoot(conversationId, QuestGraph::setSayTheRightThingTaskCompleteAndReceivePossibleTarget)
        if (possibleReceive.isTaken()) {
            endConversation(nextId)
        } else {
            endConversationAndLoad { ReceiveScreen.load(possibleReceive, quest, graph) }
        }
    }

    private fun receiveItemForQuest() {
        val quest = gameData.quests.getQuestById(conversationId)
        val receive = ConversationSpoilLoader.getLoot(conversationId, QuestGraph::receiveItemsForQuest)
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

    private fun finishAllAndRewardLinkedQuest() {
        val quest = gameData.quests.getSingleQuestByTaskWithConversationId(conversationId)
        quest.forceSetAllTasksComplete()
        val reward = gameData.loot.getLoot(quest.id)
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
        populateDialog(nextId)
    }

    private fun endConversation(nextId: String) {
        playSe(AudioEvent.SE_CONVERSATION_END)
        possibleHidePersistentTooltip()
        endConversationWithoutSound(nextId)
    }

    private fun endConversationWithoutSound(nextId: String) {
        graph.currentPhraseId = nextId
        hideWithFade()
        conversationObserver.notifyExitConversation()
    }

    private fun endConversationBeforeLoadScreen(nextId: String) {
        graph.currentPhraseId = nextId
        possibleHidePersistentTooltip()
        hide()
        conversationObserver.notifyExitConversation()
    }

    private fun endConversationAndLoad(lootScreen: () -> Unit) {
        playSe(AudioEvent.SE_CONVERSATION_END)
        possibleHidePersistentTooltip()
        stage.addAction(Actions.sequence(Actions.run { hideWithFade() },
                                         Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
                                         Actions.run { lootScreen.invoke() }))
    }

    private fun hideWithFade() {
        label.setText("")
        answers.clearItems()
        answers.clearSelectedIndexHistory()
        graph.clearChosenAnswersHistory()
        scrollPane.clearListeners()
        dialog.hide()
    }

    private fun hide() {
        answers.clearSelectedIndexHistory()
        graph.clearChosenAnswersHistory()
        scrollPane.clearListeners()
        dialog.hide(null)
    }

    private fun populateDialog(phraseId: String) {
        graph.currentPhraseId = phraseId
        populateFace()
        populateName()
        populatePhrase()
        populateChoices()
        possiblePersistentTooltip(phraseId)
    }

    private fun populateFace() {
        val phraseFace: String = graph.getCurrentFace()
        if (phraseFace.isNotBlank()) {
            val mainTable = dialog.contentTable.getChild(0) as Table
            val faceTable = mainTable.getChild(0) as Table
            val faceCell = faceTable.getCell(faceImage)
            faceImage = Utils.getFaceImage(phraseFace)
            faceCell.setActor<Actor>(faceImage)
        }
    }

    private fun populateName() {
        val phraseName: String = graph.getCurrentName()
        if (phraseName.isNotBlank()) {
            val mainTable = dialog.contentTable.getChild(0) as Table
            val faceTable = mainTable.getChild(0) as Table
            val nameCell = faceTable.getCell(nameLabel)
            nameLabel = Label(phraseName, LabelStyle(smallFont, Color.BLACK))
            nameCell.setActor<Actor>(nameLabel)
        }
    }

    private fun populatePhrase() {
        val text: String = graph.getCurrentPhrase().joinToString(System.lineSeparator())
        if (text.isNotBlank()) {
            label.restart("{COLOR=BLACK}$text")
            if (conversationId.startsWith("bury_")) {
                label.skipToTheEnd()
            }
        } else {
            label.setText("")
        }
    }

    private fun populateChoices() {
        val choices = GdxArray<ConversationChoice>(graph.getAssociatedChoices())
        answers.populateChoices(choices)
        repositionScrollPaneBasedOnContent()
    }

    private fun possiblePersistentTooltip(phraseId: String) {
        if (conversationId.contains("price-") && conversationId.substringAfterLast("-").toInt() > 0) {
            if (phraseId == "2") {
                showGoldTooltip()
            } else {
                possibleHidePersistentTooltip()
            }
        }
    }

    private fun repositionScrollPaneBasedOnContent() {
        if (label.text.isBlank() && faceId.isBlank() && graph.getCurrentFace().isBlank()) {
            rowWithScrollPane.padTop(-SCROLL_PANE_TOP_PAD).padLeft(-PAD).center()
        } else if (label.text.isBlank()) {
            rowWithScrollPane.padTop(-SCROLL_PANE_TOP_PAD).padLeft(-PAD).padRight(-(PAD * 2f))
        } else if (faceId.isBlank() && graph.getCurrentFace().isBlank()) {
            rowWithScrollPane.padTop(0f).padLeft(PAD + Constant.FACE_SIZE + PAD + ARROW_PAD_LEFT)
        } else {
            rowWithScrollPane.padTop(0f).padLeft(ARROW_PAD_LEFT)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun createLabel(): TypingLabel {
        return TypingLabel("No Conversation", LabelStyle(font, Color.BLACK))
            .apply { wrap = true }
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

    private fun showGoldTooltip() {
        val goldAmount = gameData.inventory.getTotalOfItem(ResourceType.GOLD.name)
        val message = "Gold: $goldAmount"
        conversationObserver.notifyShowPersistentMessageTooltip(message)
    }

    private fun possibleHidePersistentTooltip() {
        conversationObserver.notifyHidePersistentMessageTooltip()
    }

}
