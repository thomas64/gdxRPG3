package nl.t64.cot.components.conversation

import com.badlogic.gdx.Gdx
import nl.t64.cot.GameTest
import nl.t64.cot.ProfileManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.screens.world.conversation.ConversationSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class ConversationTest : GameTest() {

    private lateinit var conversations: ConversationContainer

    @BeforeEach
    private fun setup() {
        conversations = ConversationContainer()
    }

    @Test
    fun whenConversationsAreCreated_ShouldHaveAllConversationsStartedAtPhrase1() {
        val phraseIdContainer = conversations.createPhraseIdContainer()
        assertThat(phraseIdContainer.getPhraseId("luana01")).isEqualTo("1")
    }

    @Test
    fun whenGameIsSaved_ShouldStoreAllCurrentPhraseIdsInContainer() {
        val phraseIdContainer = conversations.createPhraseIdContainer()
        val configDirList = Gdx.files.internal("configs/conversations/").list(".json")
        val containerSize = phraseIdContainer.size
        assertThat(configDirList).hasSizeLessThan(containerSize)
    }

    @Test
    fun whenGameIsLoaded_ShouldFillConversationContainerFromPhraseIdContainer() {
        val phraseIdContainer = conversations.createPhraseIdContainer()
        phraseIdContainer.setPhraseId("luana01", "2")
        conversations.setCurrentPhraseIds(phraseIdContainer)
        val conversationGraph = conversations.getConversationById("luana01")
        assertThat(conversationGraph.currentPhraseId).isEqualTo("2")
    }

    @Test
    fun whenConversationGraphIsLoaded_ShouldGetDefaultChoices() {
        val graph = conversations.getConversationById("luana01")
        assertThat(graph.currentPhraseId).isEqualTo("1")
        assertThat(graph.phrases).hasSize(15)
        assertThat(graph.getCurrentPhrase()).containsOnly("Hi Mozes!")
        assertThat(graph.phrases["1"]!!.text).containsOnly("Hi Mozes!")
        assertThat(graph.phrases["1"]!!.choices).isEmpty()
        assertThat(graph.getAssociatedChoices()).hasSize(1)
        assertThat(graph.getAssociatedChoices()[0].text).isEqualTo("->")
        assertThat(graph.getAssociatedChoices()[0]).hasToString("->")
        assertThat(graph.getAssociatedChoices()[0].destinationId).isEqualTo("2")
        assertThat(graph.getAssociatedChoices()[0].conversationCommand).isEqualTo(ConversationCommand.NONE)
        assertThat(graph.getAssociatedChoices()[0].conditionIds).isEmpty()
        assertThat(graph.getAssociatedChoices()[0].isMeetingCondition()).isTrue
    }

    @Test
    fun whenConversationGraphIsLoaded_ShouldGetGivenChoices() {
        val graph = conversations.getConversationById("luana01")
        graph.currentPhraseId = "8"
        assertThat(graph.currentPhraseId).isEqualTo("8")
        assertThat(graph.phrases).hasSize(15)
        assertThat(graph.getCurrentPhrase()).containsOnly("Okay, here's the deal. You are taking me with you and MAYBE I'll forgive you.")
        assertThat(graph.getAssociatedChoices()).hasSize(2)
        assertThat(graph.getAssociatedChoices()[0].text).isEqualTo("Alright Luana, I will endure your presence.")
        assertThat(graph.getAssociatedChoices()[0].destinationId).isEqualTo("9")
        assertThat(graph.getAssociatedChoices()[0].conversationCommand).isEqualTo(ConversationCommand.NONE)
        assertThat(graph.getAssociatedChoices()[1].text).isEqualTo("No, I don't WANT your protection.")
        assertThat(graph.getAssociatedChoices()[1].destinationId).isEqualTo("10")
        assertThat(graph.getAssociatedChoices()[1].conversationCommand).isEqualTo(ConversationCommand.NONE)
    }

    @Test
    fun whenConversationGraphIsLoaded_ShouldGetOtherChoices() {
        val graph = conversations.getConversationById("luana01")
        graph.currentPhraseId = "9"
        assertThat(graph.currentPhraseId).isEqualTo("9")
        assertThat(graph.phrases).hasSize(15)
        assertThat(graph.getCurrentPhrase()).containsOnly("Alright! Now were talking!", "You are so not gonna regret this.")
        assertThat(graph.getAssociatedChoices()).hasSize(1)
        assertThat(graph.getAssociatedChoices()[0].text).isEqualTo("->")
        assertThat(graph.getAssociatedChoices()[0].destinationId).isEqualTo("301")
        assertThat(graph.getAssociatedChoices()[0].conversationCommand).isEqualTo(ConversationCommand.HERO_JOIN)
    }

    @Test
    fun whenConversationHasSkillCondition_ShouldMeetConditionWhenMet() {
        gameData.onNotifyCreateProfile(ProfileManager())

        val graph = conversations.getConversationById("quest0006")
        assertThat(graph.currentPhraseId).isEqualTo("1")
        assertThat(graph.getAssociatedChoices()).hasSize(3)
        assertThat(graph.getAssociatedChoices()[1].text).startsWith("[Diplomat 2] W")
        assertThat(graph.getAssociatedChoices()[1].isMeetingCondition()).isFalse

        gameData.party.addHero(gameData.heroes.getCertainHero("elias"))
        assertThat(graph.getAssociatedChoices()[1].isMeetingCondition()).isTrue
    }

    @Test
    fun whenConversationHasItemCondition_ShouldMeetConditionWhenMet() {
        gameData.onNotifyCreateProfile(ProfileManager())

        val graph = conversations.getConversationById("quest0006")
        graph.currentPhraseId = "11"
        assertThat(graph.getAssociatedChoices()).hasSize(3)
        assertThat(graph.getAssociatedChoices()[1].text).startsWith("[Mysterious Tunnel Key] Y")
        assertThat(graph.getAssociatedChoices()[1].isMeetingCondition()).isFalse

        gameData.inventory.autoSetItem(InventoryDatabase.createInventoryItem("key_mysterious_tunnel"))
        assertThat(graph.getAssociatedChoices()[1].isMeetingCondition()).isTrue
    }

    @Test
    fun whenConversationHasInvisibleQuestCondition_ShouldShowChoiceWhenMet() {
        gameData.onNotifyCreateProfile(ProfileManager())
        val graph = conversations.getConversationById("quest0006")
        graph.currentPhraseId = "37"

        assertThat(graph.getAssociatedChoices()).hasSize(1)
        assertThat(graph.getAssociatedChoices()[0].text).startsWith("I was just kidding.")
        assertThat(graph.getAssociatedChoices()[0].conditionIds).isEmpty()
        assertThat(graph.getAssociatedChoices()[0].isMeetingCondition()).isTrue

        val quest7 = gameData.quests.getQuestById("quest0007")
        quest7.know()
        quest7.accept(ConversationSubject())
        quest7.unclaim()

        assertThat(graph.getAssociatedChoices()).hasSize(2)
        assertThat(graph.getAssociatedChoices()[1].text).startsWith("I gave it to someone else. (Fails ")
        assertThat(graph.getAssociatedChoices()[1].conditionIds).containsExactly("i_quest7_unclaimed")
        assertThat(graph.getAssociatedChoices()[1].isMeetingCondition()).isTrue
    }

    @Test
    fun whenConversationHasInvisibleTaskCondition_ShouldShowChoiceWhenMet() {
        gameData.onNotifyCreateProfile(ProfileManager())
        val graph = conversations.getConversationById("quest0006")
        graph.currentPhraseId = "37"
        val choices = graph.phrases["37"]!!.choices

        assertThat(graph.getAssociatedChoices()).hasSize(1)
        assertThat(graph.getAssociatedChoices()[0].text).startsWith("I was just kidding.")
        assertThat(graph.getAssociatedChoices()[0].conditionIds).isEmpty()
        assertThat(graph.getAssociatedChoices()[0].isMeetingCondition()).isTrue

        assertThat(choices).hasSize(3)
        assertThat(choices[2].text).startsWith("I already used it myself. (Fails ")
        assertThat(choices[2].conditionIds).containsExactly("i_quest6_task3")
        assertThat(choices[2].isMeetingCondition()).isFalse
        assertThat(choices[2].isVisible()).isFalse

        val quest6 = gameData.quests.getQuestById("quest0006")
        gameData.inventory.autoSetItem(InventoryDatabase.createInventoryItem("key_mysterious_tunnel"))
        quest6.setTaskComplete("3")

        assertThat(graph.getAssociatedChoices()).hasSize(2)
        assertThat(graph.getAssociatedChoices()[1].text).startsWith("I already used it myself. (Fails ")
        assertThat(graph.getAssociatedChoices()[1].conditionIds).containsExactly("i_quest6_task3")
        assertThat(graph.getAssociatedChoices()[1].isMeetingCondition()).isTrue

        assertThat(choices).hasSize(3)
        assertThat(choices[2].text).startsWith("I already used it myself. (Fails ")
        assertThat(choices[2].conditionIds).containsExactly("i_quest6_task3")
        assertThat(choices[2].isMeetingCondition()).isTrue
        assertThat(choices[2].isVisible()).isTrue
    }

    @Test
    fun `When conversation has different faces, should show correct faces with the sentences`() {
        val graph = conversations.getConversationById("mozes_calls_grace_inside")
        assertThat(graph.getCurrentFace()).isEqualTo("mozes")
        assertThat(graph.currentPhraseId).isEqualTo("1")
        graph.currentPhraseId = "3"
        assertThat(graph.getCurrentFace()).isEqualTo("girl01")
    }

}
