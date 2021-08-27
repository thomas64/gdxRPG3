package nl.t64.cot.subjects

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.screens.world.entity.Entity


interface ComponentObserver {

    fun onNotifyShowConversationDialog(conversationId: String, npcEntity: Entity)
    fun onNotifyShowConversationDialog(conversationId: String, entityId: String)
    fun onNotifyShowNoteDialog(noteId: String)
    fun onNotifyShowFindDialog(loot: Loot, event: AudioEvent, message: String)
    fun onNotifyShowFindDialog(loot: Loot, event: AudioEvent)
    fun onNotifyShowMessageDialog(message: String)
    fun onNotifyShowBattleScreen(battleId: String)
    fun onNotifyShowBattleScreen(battleId: String, enemyEntity: Entity)

}
