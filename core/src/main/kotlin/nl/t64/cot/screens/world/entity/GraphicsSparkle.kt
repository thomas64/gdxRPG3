package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.collections.GdxArray
import ktx.collections.toGdxArray
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import kotlin.random.Random


private const val SPARKLE_PATH = "sprites/objects/sparkle.png"

class GraphicsSparkle(
    animationType: AnimationType,
    sparkleShape: SparkleShape
) : GraphicsComponent() {

    private val sparkleAnimation: Animation<TextureRegion> = createAnimation(animationType, sparkleShape)

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

    private fun createAnimation(animationType: AnimationType,
                                sparkleShape: SparkleShape
    ): Animation<TextureRegion> {
        return when (animationType) {
            AnimationType.LONG -> createLongAnimation(sparkleShape)
            AnimationType.SHORT -> createShortAnimation(sparkleShape)
            AnimationType.NONE -> createNoAnimation()
        }
    }

    private fun createLongAnimation(sparkleShape: SparkleShape): Animation<TextureRegion> {
        val textures: Array<Array<TextureRegion>> = Utils.getSplitTexture(SPARKLE_PATH, Constant.TILE_SIZE.toInt())

        val thirtyEmptyFrames: MutableList<TextureRegion> = MutableList(30) { textures[4][0] }
        val framesOfOneSparkle: List<TextureRegion> = createFramesOfOneSparkle(textures, sparkleShape)
        val firstFiveEmptyFrames = 5
        val randomIndexToPutSparkle: Int = Random(hashCode()).nextInt(firstFiveEmptyFrames, thirtyEmptyFrames.size + 1)
        thirtyEmptyFrames.addAll(randomIndexToPutSparkle, framesOfOneSparkle)
        val allTheFramesIncludingOneRandomPlacedSparkle: GdxArray<TextureRegion> = thirtyEmptyFrames.toGdxArray()

        return Animation(Constant.FAST_FRAMES, allTheFramesIncludingOneRandomPlacedSparkle, Animation.PlayMode.LOOP)
    }

    private fun createShortAnimation(sparkleShape: SparkleShape): Animation<TextureRegion> {
        val textures: Array<Array<TextureRegion>> = Utils.getSplitTexture(SPARKLE_PATH, Constant.TILE_SIZE.toInt())

        val fiveEmptyFrames: MutableList<TextureRegion> = MutableList(5) { textures[4][0] }
        val framesOfOneSparkle: List<TextureRegion> = createFramesOfOneSparkle(textures, sparkleShape)
        fiveEmptyFrames.addAll(framesOfOneSparkle)
        val fiveEmptyFramesFollowedByFiveSparkleFrames: GdxArray<TextureRegion> = fiveEmptyFrames.toGdxArray()
        return Animation(Constant.FAST_FRAMES, fiveEmptyFramesFollowedByFiveSparkleFrames, Animation.PlayMode.LOOP)
    }

    private fun createNoAnimation(): Animation<TextureRegion> {
        val textures: Array<Array<TextureRegion>> = Utils.getSplitTexture(SPARKLE_PATH, Constant.TILE_SIZE.toInt())
        return Animation(Constant.NO_FRAMES, textures[4][0])
    }

    private fun createFramesOfOneSparkle(textures: Array<Array<TextureRegion>>,
                                         sparkleShape: SparkleShape
    ): List<TextureRegion> {
        return when (sparkleShape) {
            SparkleShape.STAR -> listOf(textures[3][2],
                                        textures[3][1],
                                        textures[0][1],
                                        textures[3][1],
                                        textures[3][2])
            SparkleShape.ORB -> listOf(textures[3][0],
                                       textures[2][0],
                                       textures[3][0])
        }
    }

}
