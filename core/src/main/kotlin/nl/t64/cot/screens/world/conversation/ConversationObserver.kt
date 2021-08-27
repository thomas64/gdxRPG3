package nl.t64.cot.screens.world.conversation

import nl.t64.cot.components.loot.Loot


interface ConversationObserver {

    fun onNotifyExitConversation()

    fun onNotifyShowMessageTooltip(message: String) {
        throw IllegalStateException("Implement this method in child.")
    }

    fun onNotifyShowLevelUpDialog(message: String) {
        throw IllegalStateException("Implement this method in child.")
    }

    fun onNotifyLoadShop() {
        throw IllegalStateException("Implement this method in child.")
    }

    fun onNotifyShowRewardDialog(reward: Loot, levelUpMessage: String?) {
        throw IllegalStateException("Implement this method in child.")
    }

    fun onNotifyShowReceiveDialog(receive: Loot) {
        throw IllegalStateException("Implement this method in child.")
    }

    fun onNotifyHeroJoined() {
        throw IllegalStateException("Implement this method in child.")
    }

    fun onNotifyHeroDismiss() {
        throw IllegalStateException("Implement this method in child.")
    }

}
