package nl.t64.cot.subjects

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.screens.world.entity.Entity


interface ComponentObserver {

    fun onNotifyShowConversationDialogFromNpc(conversationId: String, npcEntity: Entity)
    fun onNotifyShowConversationDialogFromEvent(conversationId: String, entityId: String)
    fun onNotifyShowNoteDialog(noteId: String)
    fun onNotifyShowFindScreenWithMessageDialog(loot: Loot, event: AudioEvent, message: String)
    fun onNotifyShowFindScreen(loot: Loot, event: AudioEvent)
    fun onNotifyShowStorageScreen()
    fun onNotifyShowMessageDialog(message: String)
    fun onNotifyShowBattleScreen(battleId: String, enemyEntity: Entity)

}
