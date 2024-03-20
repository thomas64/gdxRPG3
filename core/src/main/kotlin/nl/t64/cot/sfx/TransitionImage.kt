package nl.t64.cot.sfx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.toDrawable


private const val TEN_TIMES_FULL_HD = 10f

class TransitionImage(
    val purpose: TransitionPurpose = TransitionPurpose.JUST_FADE,
    color: Color = Color.BLACK
) : Image() {

    init {
        super.toFront()
        super.setFillParent(true)
        super.setDrawable(color.toDrawable())
        super.clearListeners()
        super.setTouchable(Touchable.disabled)
        super.scaleBy(TEN_TIMES_FULL_HD)
    }

}
