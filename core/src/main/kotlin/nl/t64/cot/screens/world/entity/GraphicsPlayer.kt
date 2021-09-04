package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


class GraphicsPlayer : GraphicsComponent() {

    private var feetPosition = Vector2()
    private var moveSpeed: Float = Constant.MOVE_SPEED_2
    private var stepCount: Float = 0f
    private var currentTransformation: String = Constant.PLAYER_ID

    init {
        super.loadWalkingAnimation(Constant.PLAYER_ID)
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            direction = event.direction!!
        }
        if (event is StateEvent) {
            state = event.state
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
        if (event is PositionEvent) {
            position = event.position
            feetPosition = Vector2(position.x + Constant.HALF_TILE_SIZE, position.y)
        }
        if (event is SpeedEvent) {
            setNewFrameDuration(event.moveSpeed)
            moveSpeed = event.moveSpeed
        }
    }

    override fun update(dt: Float) {
        checkTransformation()
        setFrame(dt)
        playStepSoundWhenWalking(dt)
    }

    override fun render(batch: Batch) {
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = Color.BLUE
        when (direction) {
            Direction.NORTH -> shapeRenderer.triangle(position.x,
                                                      position.y,
                                                      position.x + Constant.HALF_TILE_SIZE,
                                                      position.y + Constant.TILE_SIZE,
                                                      position.x + Constant.TILE_SIZE,
                                                      position.y)
            Direction.SOUTH -> shapeRenderer.triangle(position.x,
                                                      position.y + Constant.TILE_SIZE,
                                                      position.x + Constant.TILE_SIZE,
                                                      position.y + Constant.TILE_SIZE,
                                                      position.x + Constant.HALF_TILE_SIZE,
                                                      position.y)
            Direction.WEST -> shapeRenderer.triangle(position.x,
                                                     position.y + Constant.HALF_TILE_SIZE,
                                                     position.x + Constant.TILE_SIZE,
                                                     position.y + Constant.TILE_SIZE,
                                                     position.x + Constant.TILE_SIZE,
                                                     position.y)
            Direction.EAST -> shapeRenderer.triangle(position.x,
                                                     position.y,
                                                     position.x,
                                                     position.y + Constant.TILE_SIZE,
                                                     position.x + Constant.TILE_SIZE,
                                                     position.y + Constant.HALF_TILE_SIZE)
            Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
        }
    }

    private fun playStepSoundWhenWalking(dt: Float) {
        if (state == EntityState.WALKING && moveSpeed != Constant.MOVE_SPEED_4) {
            stepCount -= dt
            if (stepCount < 0f) {
                stepCount = frameDuration * 2f
                audioManager.handle(AudioCommand.SE_PLAY_ONCE, mapManager.getGroundSound(feetPosition))
            }
        } else {
            stepCount = 0f
        }
    }

    private fun checkTransformation() {
        val newTransformation: String = gameData.party.getPlayer().getTransformation()
        if (currentTransformation != newTransformation) {
            currentTransformation = newTransformation
            super.loadWalkingAnimation(currentTransformation)
        }
    }

}
