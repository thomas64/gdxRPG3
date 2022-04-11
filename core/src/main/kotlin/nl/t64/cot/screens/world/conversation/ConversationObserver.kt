package nl.t64.cot.screens.world.conversation

import nl.t64.cot.components.loot.Loot


interface ConversationObserver {

    fun onNotifyExitConversation()

    fun onNotifyShowMessageTooltip(message: String): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyLoadShop(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyLoadAcademy(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyLoadSchool(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyShowRewardDialog(reward: Loot, levelUpMessage: String?): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyShowReceiveDialog(receive: Loot): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyHeroJoined(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyHeroDismiss(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyShowBattleScreen(battleId: String): Unit =
        throw IllegalStateException("Implement this method in child.")

}
