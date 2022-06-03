package nl.t64.cot.sfx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable


private const val TEN_TIMES_FULL_HD = 10f

class TransitionImage(
    val purpose: TransitionPurpose = TransitionPurpose.JUST_FADE,
    color: Color = Color.BLACK
) : Image() {

    init {
        super.toFront()
        super.setFillParent(true)

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        super.setDrawable(TextureRegionDrawable(TextureRegion(Texture(pixmap))))
        pixmap.dispose()
        super.clearListeners()
        super.setTouchable(Touchable.disabled)
        super.scaleBy(TEN_TIMES_FULL_HD)
    }

}
