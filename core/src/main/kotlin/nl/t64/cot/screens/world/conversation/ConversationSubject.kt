package nl.t64.cot.screens.world.conversation

import nl.t64.cot.components.loot.Loot


class ConversationSubject {

    private val observers: MutableList<ConversationObserver> = ArrayList()

    fun addObserver(observer: ConversationObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: ConversationObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyExitConversation() {
        observers.forEach { it.onNotifyExitConversation() }
    }

    fun notifyShowMessageTooltip(message: String) {
        observers.forEach { it.onNotifyShowMessageTooltip(message) }
    }

    fun notifyShowLevelUpDialog(message: String) {
        observers.forEach { it.onNotifyShowLevelUpDialog(message) }
    }

    fun notifyLoadShop() {
        observers.forEach { it.onNotifyLoadShop() }
    }

    fun notifyLoadAcademy() {
        observers.forEach { it.onNotifyLoadAcademy() }
    }

    fun notifyLoadSchool() {
        observers.forEach { it.onNotifyLoadSchool() }
    }

    fun notifyShowRewardDialog(reward: Loot, levelUpMessage: String?) {
        observers.forEach { it.onNotifyShowRewardDialog(reward, levelUpMessage) }
    }

    fun notifyShowReceiveDialog(receive: Loot) {
        observers.forEach { it.onNotifyShowReceiveDialog(receive) }
    }

    fun notifyHeroJoined() {
        observers.forEach { it.onNotifyHeroJoined() }
    }

    fun notifyHeroDismiss() {
        observers.forEach { it.onNotifyHeroDismiss() }
    }

}
