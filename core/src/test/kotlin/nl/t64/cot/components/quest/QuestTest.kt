package nl.t64.cot.components.quest

import nl.t64.cot.GameTest
import nl.t64.cot.ProfileManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.loot.LootContainer
import nl.t64.cot.components.party.inventory.InventoryContainer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class QuestTest : GameTest() {

    private lateinit var quests: QuestContainer
    private lateinit var inventory: InventoryContainer
    private lateinit var loot: LootContainer

    @BeforeEach
    private fun beforeEach() {
        gameData.onNotifyCreateProfile(ProfileManager())
        quests = gameData.quests
        inventory = gameData.inventory
        party = gameData.party
        loot = gameData.loot
    }

    @Test
    fun whenQuestsAreCreated_ShouldHaveAllQuestsStartedAtStateUnknown() {
        assertThat(quests.getQuestById("quest0001").currentState).isEqualTo(QuestState.UNKNOWN)
    }

    @Test
    fun whenQuestGraphIsLoaded_ShouldHaveCorrectData() {
        val quest0001 = quests.getQuestById("quest0001")
        assertThat(quest0001.id).isEqualTo("quest0001")
        assertThat(quest0001.currentState).isEqualTo(QuestState.UNKNOWN)
        assertThat(quest0001.title).isEqualTo("Herbs for boy")
        assertThat(quest0001.entityId).isEqualTo("boy01")
        assertThat(quest0001.summary).contains("Johnny's mother is ill and ")
        assertThat(quest0001).hasToString("     Herbs for boy")
        val tasks = quest0001.tasks
        assertThat(tasks).hasSize(3)
        assertThat(tasks["1"]!!.taskPhrase).isEqualTo("Collect 3 herbs")
        assertThat(tasks["1"]!!.type).isEqualTo(QuestTaskType.FETCH_ITEM)
        assertThat(tasks["1"]!!.target).containsOnly(entry("herb", 3))
        assertThat(tasks["1"]).hasToString("     Collect 3 herbs")
    }

//    @Test
//    fun whenQuestGraphIsLoaded_ShouldHandleFlow() {
//        assertThat(party.getHero(0).xpToInvest).isZero
//        val quest0001 = quests.getQuestById("quest0001")
//        assertThatIllegalArgumentException().isThrownBy { quest0001.setTaskComplete("1") }
//        assertThat(quest0001.isCurrentStateEqualOrHigherThan(QuestState.FINISHED)).isFalse
//        quest0001.handleAccept { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_ACCEPT) }
//        assertThat(quest0001.currentState).isEqualTo(QuestState.ACCEPTED)
//        quest0001.handleReturn { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_NO_SUCCESS) }
//        assertThatIllegalStateException().isThrownBy { quest0001.handleReward({}, ConversationSubject()) }
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("gemstone", 5))
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("herb", 3))
//        quest0001.handleReturn { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_SUCCESS) }
//        quest0001.handleReward({}, ConversationSubject())
//        assertThat(quest0001.currentState).isEqualTo(QuestState.UNCLAIMED)
//        loot.getLoot("quest0001").clearContent()
//        quest0001.handleReward({ assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_FINISHED) },
//                               ConversationSubject())
//        assertThat(quest0001.currentState).isEqualTo(QuestState.FINISHED)
//        assertThat(quest0001.isCurrentStateEqualOrHigherThan(QuestState.FINISHED)).isTrue
//        assertThat(party.getHero(0).xpToInvest).isEqualTo(2)
//        assertThat(inventory.hasEnoughOfItem("gemstone", 1)).isFalse
//        assertThat(inventory.hasEnoughOfItem("herb", 1)).isFalse
//    }
//
//    @Test
//    fun whenDemandsAreCompleteFromTheStart_ShouldHandleQuickFlow() {
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("gemstone", 5))
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("herb", 3))
//        val quest0001 = quests.getQuestById("quest0001")
//        quest0001.handleAccept { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_IMMEDIATE_SUCCESS) }
//    }
//
//    @Test
//    fun whenQuestIsTolerated_ShouldNotHandleQuickFlow() {
//        val quest0002 = quests.getQuestById("quest0002")
//        assertThat(quest0002.currentState).isEqualTo(QuestState.UNKNOWN)
//        quest0002.handleTolerate()
//        assertThat(quest0002.currentState).isEqualTo(QuestState.ACCEPTED)
//    }
//
//    @Test
//    fun whenSearchingForEdgeCases_ShouldThrowExceptions() {
//        val quest0001 = quests.getQuestById("quest0001")
//        assertThat(quest0001.currentState).isEqualTo(QuestState.UNKNOWN)
//        quest0001.know()
//        assertThat(quest0001.currentState).isEqualTo(QuestState.KNOWN)
//        quest0001.know()
//        assertThat(quest0001.currentState).isEqualTo(QuestState.KNOWN)
//        assertThatExceptionOfType(NoSuchElementException::class.java)
//            .isThrownBy { quest0001.handleReceive(ConversationSubject()) }
//        quest0001.handleAccept {}
//        assertThat(quest0001.currentState).isEqualTo(QuestState.ACCEPTED)
//        assertThatIllegalStateException().isThrownBy { quest0001.know() }
//        assertThatIllegalStateException().isThrownBy { quest0001.accept() }
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("gemstone", 5))
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("herb", 3))
//        quest0001.handleReward({}, ConversationSubject())
//        assertThat(quest0001.currentState).isEqualTo(QuestState.UNCLAIMED)
//        assertThatIllegalStateException().isThrownBy { quest0001.unclaim() }
//        loot.getLoot("quest0001").clearContent()
//        quest0001.handleReward({}, ConversationSubject())
//        assertThat(quest0001.currentState).isEqualTo(QuestState.FINISHED)
//        assertThatIllegalStateException().isThrownBy { quest0001.finish() }
//    }
//
//    @Test
//    fun whenQuestReceiveItemQuestIsHandled_ShouldBeHandled() {
//        val quest0005 = quests.getQuestById("quest0005")
//        quest0005.handleReceive(ConversationSubject())
//        assertThat(quest0005.currentState).isEqualTo(QuestState.KNOWN)
//
//        quest0005.handleCheckIfAcceptedInventory(
//            "1", "xxx", { assertThat(it).isNull() }, { assertThat(it).isEqualTo("xxx") })
//
//        val targetId = quest0005.tasks["1"]!!.target.iterator().next().key
//        val targetItem = InventoryDatabase.createInventoryItem(targetId)
//        inventory.autoSetItem(targetItem)
//
//        quest0005.handleCheckIfAcceptedInventory(
//            "1", "xxx", { assertThat(it).isNull() }, { assertThat(it).isEqualTo("xxx") })
//
//        quest0005.accept()
//
//        quest0005.handleCheckIfAcceptedInventory(
//            "1", "xxx",
//            { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_DELIVERY) }, { assertThat(it).isNull() })
//
//        assertThat(inventory.contains(mapOf(Pair(targetId, 1)))).isTrue
//        quest0005.setTaskComplete("1")
//        assertThat(inventory.contains(mapOf(Pair(targetId, 1)))).isFalse
//    }
//
//    @Test
//    fun whenQuestReceiveMessageQuestIsHandled_ShouldBeHandled() {
//        val quest0004 = quests.getQuestById("quest0004")
//
//        quest0004.handleCheckIfAccepted(
//            "xxx", { assertThat(it).isNull() }, { s: String? -> assertThat(s).isEqualTo("xxx") })
//
//        quest0004.handleAccept { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_ACCEPT) }
//
//        quest0004.handleCheckIfAccepted(
//            "xxx", { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_DELIVERY) }, { assertThat(it).isNull() })
//    }
//
//    @Test
//    fun whenPartyGainsEnoughXp_ShouldShowMessageThatMemberGainedLevel() {
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("gemstone", 5))
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("herb", 3))
//        party.getHero(0).gainXp(19, StringBuilder())
//        val quest0001 = quests.getQuestById("quest0001")
//        val questLoot = loot.getLoot("quest0001")
//        assertThat(questLoot.isXpGained()).isFalse
//        quest0001.handleAccept {}
//        quest0001.handleReturn {}
//        questLoot.clearContent()
//        quest0001.handleReward({}, ConversationSubject())
//        assertThat(questLoot.isXpGained()).isTrue
//    }
//
//    @Test
//    fun whenQuestFails_ShouldSetSubtasksAccordingly() {
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("key_mysterious_tunnel", 1))
//
//        val quest0006 = quests.getQuestById("quest0006")
//        assertThat(quest0006.getAllQuestTasksForVisual()).extracting("isOptional")
//            .containsExactly(true, false, true, false)
//        quest0006.handleAccept {}
//        val allTasks = quest0006.getAllQuestTasksForVisual()
//        assertThat(allTasks[0].isFailed).isFalse
//        assertThat(allTasks[1].isFailed).isFalse
//        assertThat(allTasks[2].isFailed).isFalse
//        assertThat(allTasks[3].isFailed).isFalse
//        quest0006.handleReward({}, ConversationSubject())
//        assertThat(allTasks[0].isFailed).isTrue
//        assertThat(allTasks[0]).hasToString("x  [Optional] Search for clues about the key")
//        assertThat(allTasks[1].isFailed).isFalse
//        assertThat(allTasks[2].isFailed).isTrue
//        assertThat(allTasks[3].isFailed).isFalse
//    }
//
//    @Test
//    fun whenQuestTaskIsCheckOrDiscover_ShouldSucceedToSetTaskComplete() {
//        val quest0003 = quests.getQuestById("quest0003")
//        quest0003.setTaskComplete("1")
//        quest0003.setTaskComplete("2")
//        quest0003.handleReturn { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_SUCCESS) }
//    }
//
//    @Test
//    fun whenQuestsAreNotUnknown_ShouldBecomeVisibleInQuestLogInCorrectOrder() {
//        val quest0001 = quests.getQuestById("quest0001")
//        val quest0003 = quests.getQuestById("quest0003")
//        assertThat(quests.getAllKnownQuestsForVisual()).isEmpty()
//        quest0001.know()
//        assertThat(quests.getAllKnownQuestsForVisual()).extracting("id").containsExactly("quest0001")
//        quest0003.know()
//        quest0003.accept()
//        assertThat(quests.getAllKnownQuestsForVisual()).extracting("id").containsExactly("quest0001", "quest0003")
//        quest0001.accept()
//        quest0001.unclaim()
//        assertThat(quests.getAllKnownQuestsForVisual()).extracting("id").containsExactly("quest0003", "quest0001")
//        quest0003.handleFail()
//        assertThat(quests.getAllKnownQuestsForVisual()).extracting("id").containsExactly("quest0001", "quest0003")
//    }
//
//    @Test
//    fun whenStatesOfQuestChanges_ShouldChangeToString() {
//        val quest0001 = quests.getQuestById("quest0001")
//        assertThat(quest0001).hasToString("     Herbs for boy")
//        quest0001.know()
//        assertThat(quest0001).hasToString("     Herbs for boy")
//        quest0001.accept()
//        assertThat(quest0001).hasToString("     Herbs for boy")
//        quest0001.unclaim()
//        assertThat(quest0001).hasToString("o   Herbs for boy")
//        quest0001.finish()
//        assertThat(quest0001).hasToString("v  Herbs for boy")
//        assertThat(quest0001.isFailed).isFalse
//        quest0001.handleFail()
//        assertThat(quest0001).hasToString("x  Herbs for boy")
//        assertThat(quest0001.isFailed).isTrue
//    }
//
//    @Test
//    fun whenQuestTaskIsCompleted_ShouldChangeToString() {
//        val quest0001 = quests.getQuestById("quest0001")
//        assertThat(quest0001.tasks["1"]!!.isQuestFinished).isFalse
//        assertThat(quest0001.tasks["1"]).hasToString("     Collect 3 herbs")
//
//        inventory.autoSetItem(InventoryDatabase.createInventoryItem("herb", 3))
//        assertThat(quest0001.tasks["1"]).hasToString("v  Collect 3 herbs")
//
//        inventory.autoRemoveItem("herb", 3)
//        assertThat(quest0001.tasks["1"]).hasToString("     Collect 3 herbs")
//
//        quest0001.tasks["1"]!!.forceFinished()
//        assertThat(quest0001.tasks["1"]).hasToString("v  Collect 3 herbs")
//
//        assertThat(quest0001.tasks["3"]).hasToString("     Give them to Johnny at Starter Path")
//        assertThat(quest0001.tasks["1"]!!.isQuestFinished).isTrue
//    }

    @Test
    fun whenQuestIsShownInQuestLog_ShouldShowTasksInCorrectOrder() {
        val quest0001 = quests.getQuestById("quest0001")
        assertThat(quest0001.getAllQuestTasksForVisual()).extracting("taskPhrase").containsExactly(
            "Collect 3 herbs", "Collect 5 gemstones for the programmer", "Give them to Johnny at Starter Path"
        )
    }

    @Test
    fun whenSetCompletableTaskIsSetComplete_ShouldBeComplete() {
        val quest0003 = quests.getQuestById("quest0003")
        assertThat(quest0003.isTaskComplete("1")).isFalse
        quest0003.setTaskComplete("1")
        assertThat(quest0003.isTaskComplete("1")).isTrue
    }

//    @Test
//    fun whenTwoQuestsAreLinked_ShouldChangeConversationIfLinkedQuestIsKnown() {
//        val quest0006 = quests.getQuestById("quest0006")
//        val quest0007 = quests.getQuestById("quest0007")
//        assertThat(quest0006.linkedWith).isEqualTo("quest0007")
//        assertThat(quest0007.linkedWith).isEqualTo("quest0006")
//
//        quest0006.handleCheckIfLinkedIsKnown("xxx") { assertThat(it).isEqualTo("xxx") }
//        quest0007.know()
//        quest0006.handleCheckIfLinkedIsKnown("xxx") { assertThat(it).isEqualTo(Constant.PHRASE_ID_QUEST_LINKED) }
//    }

    @Test
    fun whenQuestTaskWithMessageIsCreated_ShouldShowThatMessage() {
        val message = "(No tasks visible until this quest is accepted)"
        val questTask = QuestTask(message)
        assertThat(questTask.type).isEqualTo(QuestTaskType.NONE)
        assertThat(questTask).hasToString(
            System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + message
        )
    }

//    @Test
//    fun `When QuestTask with wrong type checks for isCompleteForReturn, should throw exception`() {
//        val task = QuestTask()
//        assertThatIllegalArgumentException().isThrownBy { task.isCompleteForReturn() }
//            .withMessage("No 'NONE' task for now.")
//    }

}
