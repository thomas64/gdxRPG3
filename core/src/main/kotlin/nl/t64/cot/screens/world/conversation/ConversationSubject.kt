package nl.t64.cot.screens.world.conversation

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.sfx.TransitionPurpose


class ConversationSubject(private val observer: ConversationObserver) {

    fun notifyExitConversation() {
        observer.onNotifyExitConversation()
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

    fun notifyJustFadeAndReloadNpcs() {
        observer.onNotifyJustFadeAndReloadNpcs()
    }

    fun notifyFade(transitionColor: Color = Color.BLACK,
                   duration: Float = 0f,
                   transitionPurpose: TransitionPurpose = TransitionPurpose.JUST_FADE,
                   actionDuringFade: () -> Unit = {},
                   actionAfterFade: () -> Unit = {}
    ) {
        observer.onNotifyFade(transitionColor = transitionColor,
                              duration = duration,
                              transitionPurpose = transitionPurpose,
                              actionDuringFade = actionDuringFade,
                              actionAfterFade = actionAfterFade)
    }

}
