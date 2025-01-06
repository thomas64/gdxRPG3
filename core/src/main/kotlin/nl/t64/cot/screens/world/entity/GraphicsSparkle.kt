package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Array
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import kotlin.random.Random


private const val SPARKLE_PATH = "sprites/objects/sparkle.png"

class GraphicsSparkle(animationType: AnimationType) : GraphicsComponent() {

    private val sparkleAnimation: Animation<TextureRegion> = createAnimation(animationType)

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            position = event.position
        }
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
        currentFrame = sparkleAnimation.getKeyFrame(frameTime)
    }

    private fun createAnimation(animationType: AnimationType): Animation<TextureRegion> {
        return when (animationType) {
            AnimationType.LONG -> createLongAnimation()
            AnimationType.SHORT -> createShortAnimation()
            AnimationType.NONE -> createNoAnimation()
        }
    }

    private fun createLongAnimation(): Animation<TextureRegion> {
        val textures = Utils.getSplitTexture(SPARKLE_PATH, Constant.TILE_SIZE.toInt())

        val thirtyEmptyFrames: MutableList<TextureRegion> = MutableList(30) { textures[4][0] }
        val framesOfOneSparkle: List<TextureRegion> = listOf(textures[3][2],
                                                             textures[3][1],
                                                             textures[0][1],
                                                             textures[3][1],
                                                             textures[3][2])
        val firstFiveEmptyFrames = 5
        val randomIndexToPutSparkle: Int = Random(System.currentTimeMillis()).nextInt(firstFiveEmptyFrames,
                                                                                      thirtyEmptyFrames.size + 1)
        thirtyEmptyFrames.addAll(randomIndexToPutSparkle, framesOfOneSparkle)
        val allTheFramesIncludingOneRandomPlacedSparkle: Array<TextureRegion> = Array(thirtyEmptyFrames.toTypedArray())

        return Animation(Constant.FAST_FRAMES, allTheFramesIncludingOneRandomPlacedSparkle, Animation.PlayMode.LOOP)
    }

    private fun createShortAnimation(): Animation<TextureRegion> {
        val textures = Utils.getSplitTexture(SPARKLE_PATH, Constant.TILE_SIZE.toInt())

        val fiveEmptyFrames: MutableList<TextureRegion> = MutableList(5) { textures[4][0] }
        val framesOfOneSparkle: List<TextureRegion> = listOf(textures[3][2],
                                                             textures[3][1],
                                                             textures[0][1],
                                                             textures[3][1],
                                                             textures[3][2])
        fiveEmptyFrames.addAll(framesOfOneSparkle)
        val fiveEmptyFramesFollowedByFiveSparkleFrames: Array<TextureRegion> = Array(fiveEmptyFrames.toTypedArray())
        return Animation(Constant.FAST_FRAMES, fiveEmptyFramesFollowedByFiveSparkleFrames, Animation.PlayMode.LOOP)
    }

    private fun createNoAnimation(): Animation<TextureRegion> {
        val textures = Utils.getSplitTexture(SPARKLE_PATH, Constant.TILE_SIZE.toInt())
        return Animation(Constant.NO_FRAMES, textures[4][0])
    }

}
