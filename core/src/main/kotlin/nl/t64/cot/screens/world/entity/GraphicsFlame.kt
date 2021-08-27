package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Array
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event


private const val FLAME_PATH = "sprites/objects/flame.png"

class GraphicsFlame : GraphicsComponent() {

    private val burnAnimation: Animation<TextureRegion> = createAnimation()

    override fun receive(event: Event) {
        // empty
    }

    override fun update(dt: Float) {
        setFrame(dt)
    }

    override fun render(batch: Batch) {
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        // empty
    }

    override fun setFrame(dt: Float) {
        frameTime = (frameTime + dt) % 12
        currentFrame = burnAnimation.getKeyFrame(frameTime)
    }

    override fun getAnimation(): Animation<TextureRegion> {
        return burnAnimation
    }

    private fun createAnimation(): Animation<TextureRegion> {
        val textures = Utils.getSplitTexture(FLAME_PATH, Constant.SPRITE_GROUP_WIDTH, Constant.TILE_SIZE.toInt())
        val region = textures[0][3]
        val split = region.split(Constant.TILE_SIZE.toInt(), Constant.TILE_SIZE.toInt())
        val frames = Array(arrayOf(split[0][0], split[0][1], split[0][2], split[0][1]))
        return Animation(Constant.FAST_FRAMES, frames, Animation.PlayMode.LOOP)
    }

}
