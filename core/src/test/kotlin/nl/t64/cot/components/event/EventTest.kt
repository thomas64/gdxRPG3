package nl.t64.cot.components.event

import nl.t64.cot.GameTest
import nl.t64.cot.ProfileManager
import nl.t64.cot.Utils.gameData
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class EventTest : GameTest() {

    private lateinit var eventContainer: EventContainer

    @BeforeEach
    private fun setup() {
        gameData.onNotifyCreateProfile(ProfileManager())
        eventContainer = EventContainer()
    }

    @Test
    fun `When event meets conditions, should be able to play`() {
        val event = eventContainer.getEventById("event0001")
        assertThat(event.conversationId).isEqualTo("event0001")
        assertThat(event.entityId).isEqualTo("man02")

        assertThat(event.conditionIds).containsExactly("quest6_known", "quest7_unknown")
        assertThat(event.hasPlayed).isFalse

        event.possibleStart()
        assertThat(event.hasPlayed).isFalse

        gameData.quests.getQuestById("quest0006").know()
        event.possibleStart()
        assertThat(event.hasPlayed).isTrue
    }

    @Test
    fun `When messagebox event, should be able to play`() {
        val event = eventContainer.getEventById("guide_event_action")
        assertThat(event.text).contains("Press %action% for an action.")
        assertThat(event.hasPlayed).isFalse
        event.possibleStart()
        assertThat(event.hasPlayed).isTrue
    }

    @Test
    fun `When text contains specific percentage signs, should replace text`() {
        val event1 = eventContainer.getEventById("guide_event_action")
        assertThat(event1.text).contains("Press %action% for an action.")
        val replace1 = TextReplacer.replace(event1.text)
        assertThat(replace1).contains("Press 'A' key for an action.")

        val event2 = eventContainer.getEventById("guide_event_inventory")
        assertThat(event2.text).contains("Press %inventory% to see your inventory,")
        val replace2 = TextReplacer.replace(event2.text)
        assertThat(replace2).contains("Press 'I' key to see your inventory,")

        val event3 = eventContainer.getEventById("guide_event_slow")
        assertThat(event3.text).contains("Keep %slow% pressed to move stealthily.")
        val replace3 = TextReplacer.replace(event3.text)
        assertThat(replace3).contains("Keep 'Ctrl' key pressed to move stealthily.")

        assertThatIllegalArgumentException().isThrownBy { TextReplacer.replace(listOf("%pipo%")) }
            .withMessage("Unexpected value: '%pipo%'")
    }

    @Test
    fun `When event type does not exist, should throw exception`() {
        val event = Event("nonexistent type")
        assertThatIllegalArgumentException().isThrownBy { event.possibleStart() }
            .withMessage("Event does not recognize type: 'nonexistent type'.")
    }

}
