package nl.t64.cot.subjects

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.screens.world.entity.Entity


class ComponentSubject {

    private val observers: MutableList<ComponentObserver> = ArrayList()

    fun addObserver(observer: ComponentObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: ComponentObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyShowConversationDialog(conversationId: String, npcEntity: Entity) {
        observers.forEach { it.onNotifyShowConversationDialogFromNpc(conversationId, npcEntity) }
    }

    fun notifyShowConversationDialog(conversationId: String, entityId: String) {
        observers.forEach { it.onNotifyShowConversationDialogFromEvent(conversationId, entityId) }
    }

    fun notifyShowNoteDialog(noteId: String) {
        observers.forEach { it.onNotifyShowNoteDialog(noteId) }
    }

    fun notifyShowFindScreenWithMessageDialog(loot: Loot, event: AudioEvent, message: String) {
        observers.forEach { it.onNotifyShowFindScreenWithMessageDialog(loot, event, message) }
    }

    fun notifyShowFindScreen(loot: Loot, event: AudioEvent) {
        observers.forEach { it.onNotifyShowFindScreen(loot, event) }
    }

    fun notifyShowStorageScreen() {
        observers.forEach { it.onNotifyShowStorageScreen() }
    }

    fun notifyShowWarpScreen(currentMapName: String) {
        observers.forEach { it.onNotifyShowWarpScreen(currentMapName) }
    }

    fun notifyShowMessageDialog(message: String, actionAfterHide: () -> Unit = {}) {
        observers.forEach { it.onNotifyShowMessageDialog(message, actionAfterHide) }
    }

    fun notifyShowBattleScreen(battleId: String, enemyEntity: Entity) {
        observers.forEach { it.onNotifyShowBattleScreen(battleId, enemyEntity) }
    }

}
