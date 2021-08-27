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
        observers.forEach { it.onNotifyShowConversationDialog(conversationId, npcEntity) }
    }

    fun notifyShowConversationDialog(conversationId: String, entityId: String) {
        observers.forEach { it.onNotifyShowConversationDialog(conversationId, entityId) }
    }

    fun notifyShowNoteDialog(noteId: String) {
        observers.forEach { it.onNotifyShowNoteDialog(noteId) }
    }

    fun notifyShowFindDialog(loot: Loot, event: AudioEvent, message: String) {
        observers.forEach { it.onNotifyShowFindDialog(loot, event, message) }
    }

    fun notifyShowFindDialog(loot: Loot, event: AudioEvent) {
        observers.forEach { it.onNotifyShowFindDialog(loot, event) }
    }

    fun notifyShowMessageDialog(message: String) {
        observers.forEach { it.onNotifyShowMessageDialog(message) }
    }

    fun notifyShowBattleScreen(battleId: String) {
        observers.forEach { it.onNotifyShowBattleScreen(battleId) }
    }

    fun notifyShowBattleScreen(battleId: String, enemyEntity: Entity) {
        observers.forEach { it.onNotifyShowBattleScreen(battleId, enemyEntity) }
    }

}
