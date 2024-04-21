package nl.t64.cot.components.conversation

import nl.t64.cot.resources.ConfigDataLoader


object NoteDatabase {

    private val notes: Map<String, ConversationGraph> = ConfigDataLoader.createNotes()

    fun getNoteById(noteId: String): ConversationGraph = notes[noteId]!!

}
