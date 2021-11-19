package nl.t64.cot.screens.world.conversation

import nl.t64.cot.components.loot.Loot


class ConversationSubject(private val observer: ConversationObserver) {

    fun notifyExitConversation() {
        observer.onNotifyExitConversation()
    }

    fun notifyShowMessageTooltip(message: String) {
        observer.onNotifyShowMessageTooltip(message)
    }

    fun notifyShowLevelUpDialog(message: String) {
        observer.onNotifyShowLevelUpDialog(message)
    }

    fun notifyLoadShop() {
        observer.onNotifyLoadShop()
    }

    fun notifyLoadAcademy() {
        observer.onNotifyLoadAcademy()
    }

    fun notifyLoadSchool() {
        observer.onNotifyLoadSchool()
    }

    fun notifyShowRewardDialog(reward: Loot, levelUpMessage: String?) {
        observer.onNotifyShowRewardDialog(reward, levelUpMessage)
    }

    fun notifyShowReceiveDialog(receive: Loot) {
        observer.onNotifyShowReceiveDialog(receive)
    }

    fun notifyHeroJoined() {
        observer.onNotifyHeroJoined()
    }

    fun notifyHeroDismiss() {
        observer.onNotifyHeroDismiss()
    }

    fun notifyShowBattleScreen(battleId: String) {
        observer.onNotifyShowBattleScreen(battleId)
    }

}
