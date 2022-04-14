package nl.t64.cot.screens.world.conversation


class ConversationSubject(private val observer: ConversationObserver) {

    fun notifyExitConversation() {
        observer.onNotifyExitConversation()
    }

    fun notifyShowMessageTooltip(message: String) {
        observer.onNotifyShowMessageTooltip(message)
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
