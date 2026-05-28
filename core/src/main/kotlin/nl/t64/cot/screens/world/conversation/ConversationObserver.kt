package nl.t64.cot.screens.world.conversation

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.sfx.TransitionPurpose


interface ConversationObserver {

    fun onNotifyExitConversation()

    fun onNotifyHeroJoined(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyHeroDismiss(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyShowBattleScreen(battleId: String): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyFadeAndReloadNpcs(duration: Float): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyFadeAndReloadNpcsPlusOneMinute(): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyFade(transitionColor: Color,
                     duration: Float,
                     transitionPurpose: TransitionPurpose,
                     actionDuringFade: () -> Unit,
                     actionAfterFade: () -> Unit
    ): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyShowPersistentMessageTooltip(message: String): Unit =
        throw IllegalStateException("Implement this method in child.")

    fun onNotifyHidePersistentMessageTooltip() {
        // empty, do nothing here unless overridden in child.
    }

}
