package nl.t64.cot.components.conversation

import nl.t64.cot.GameTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class NoteTest : GameTest() {

    @Test
    fun whenNoteDatabaseIsCreated_ShouldContainNotes() {
        val graph = NoteDatabase.getNoteById("note_statue_hero_s_hometown_hero_s_chamber")
        assertThat(graph.currentPhraseId).isEqualTo("1")
        assertThat(graph.phrases).hasSize(2)
        assertThat(graph.getCurrentPhrase()).containsOnly("What a beautiful statue.", "Somehow it looks a little bit like you.")
        assertThat(graph.getAssociatedChoices()).hasSize(1)
        graph.currentPhraseId = "2"
        assertThat(graph.getCurrentPhrase()).containsOnly("That's strange...")
        assertThat(graph.getAssociatedChoices()).hasSize(1)
        assertThat(graph.getAssociatedChoices()[0].text).isEqualTo("->")
        assertThat(graph.getAssociatedChoices()[0]).hasToString("->")
        assertThat(graph.getAssociatedChoices()[0].nextId).isEqualTo("1")
        assertThat(graph.getAssociatedChoices()[0].command).isEqualTo(ConversationCommand.EXIT)
    }

}
