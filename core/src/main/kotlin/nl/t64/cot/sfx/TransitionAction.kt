package nl.t64.cot.sfx

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.constants.Constant


class TransitionAction(
    val type: TransitionType,
    val duration: Float = Constant.FADE_DURATION
) : Action() {

    override fun act(dt: Float): Boolean {
        val actor = super.getTarget()
        when (type) {
            TransitionType.FADE_IN -> actor.addAction(Actions.fadeOut(duration))
            TransitionType.FADE_OUT -> actor.addAction(Actions.fadeIn(duration))
        }
        return true
    }

}
