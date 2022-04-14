package nl.t64.cot.screens.world.conversation


interface ConversationObserver {

    fun onNotifyExitConversation()

    fun onNotifyShowMessageTooltip(message: String): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyHeroJoined(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyHeroDismiss(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyShowBattleScreen(battleId: String): Unit =
        throw IllegalStateException("Implement this method in child.")

}
