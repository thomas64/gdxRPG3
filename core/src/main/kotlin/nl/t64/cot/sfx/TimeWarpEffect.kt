package nl.t64.cot.sfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.constants.Constant


private const val VERT_PATH = "shaders/timewarp.vert"
private const val FRAG_PATH = "shaders/timewarp.frag"
private const val EFFECT_DURATION = 2.5f

/**
 * Renders a frozen snapshot of the screen through a temporal-rewind shader:
 * a magical clock (backward-sweeping hands, glowing hub, hour ticks and pulsing
 * time rings) over a continuously swirling world, all dissolving into gray,
 * centred on ([centerX], [centerY]) in screen space (0..1).
 * Add it to a stage and call [play] to run the effect.
 */
class TimeWarpEffect(
    private val centerX: Float,
    private val centerY: Float
) : Image(), Transition {

    override val purpose: TransitionPurpose = TransitionPurpose.MAP_CHANGE

    private val region: TextureRegion = ScreenUtils.getFrameBufferTexture().apply {
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }
    private val shader: ShaderProgram = ShaderProgram(
        Gdx.files.internal(VERT_PATH),
        Gdx.files.internal(FRAG_PATH)
    )
    private var elapsed = 0f

    init {
        check(shader.isCompiled) { "TimeWarp shader failed to compile: ${shader.log}" }
        setDrawable(TextureRegionDrawable(region))
        setFillParent(true)
        setTouchable(Touchable.disabled)
        toFront()
    }

    /**
     * Runs the full effect: warp, then [actionAfterFade] (e.g. swap the map) before
     * fading out to reveal whatever is behind, and finally cleaning itself up.
     */
    fun play(actionAfterFade: () -> Unit) {
        addAction(Actions.sequence(
            Actions.delay(EFFECT_DURATION),
            Actions.run(actionAfterFade),
            Actions.fadeOut(Constant.FADE_DURATION),
            Actions.run { dispose() },
            Actions.removeActor()
        ))
    }

    override fun act(delta: Float) {
        super.act(delta)
        elapsed += delta
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val progress = (elapsed / EFFECT_DURATION).coerceIn(0f, 1f)
        val previousShader = batch.shader
        batch.shader = shader
        shader.setUniformf("u_progress", progress)
        shader.setUniformf("u_time", elapsed)
        shader.setUniformf("u_center", centerX, centerY)
        shader.setUniformf("u_aspect", Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
        shader.setUniformf("u_texMin", region.u, region.v2)
        shader.setUniformf("u_texMax", region.u2, region.v)
        super.draw(batch, parentAlpha)
        batch.shader = previousShader
    }

    private fun dispose() {
        region.texture.dispose()
        shader.dispose()
    }
}
