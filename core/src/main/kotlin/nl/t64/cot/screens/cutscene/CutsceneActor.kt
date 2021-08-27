package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils.gameData
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.DirectionEvent
import nl.t64.cot.screens.world.entity.events.StateEvent


class CutsceneActor : Image {

    private var stateTime: Float
    private val entity: Entity
    var entityState: EntityState
    var direction: Direction

    private constructor(entity: Entity, direction: Direction) {
        this.stateTime = Constant.NO_FRAMES
        this.entity = entity
        this.entityState = EntityState.IDLE
        this.direction = direction
        this.entity.send(StateEvent(this.entityState))
        this.entity.send(DirectionEvent(this.direction))

        val keyFrame = this.entity.getAnimation().getKeyFrame(stateTime)
        super.setDrawable(TextureRegionDrawable(keyFrame))
        super.setScaling(Scaling.stretch)
        super.setAlign(Align.center)
        super.setSize(super.getPrefWidth(), super.getPrefHeight())
        super.setVisible(false)
    }

    private constructor(entity: Entity, entityState: EntityState) {
        this.stateTime = Constant.NO_FRAMES
        this.entity = entity
        this.entityState = entityState
        this.direction = Direction.NONE

        val keyFrame = this.entity.getAnimation().getKeyFrame(stateTime)
        super.setDrawable(TextureRegionDrawable(keyFrame))
        super.setScaling(Scaling.stretch)
        super.setAlign(Align.center)
        super.setSize(super.getPrefWidth(), super.getPrefHeight())
        super.setVisible(true)
    }

    companion object {
        fun createCharacter(characterId: String): CutsceneActor {
            val entity = Entity(characterId, InputEmpty(), PhysicsNpc(), GraphicsNpc(characterId))
            return CutsceneActor(entity, Direction.SOUTH)
        }

        fun createDoor(doorId: String): CutsceneActor {
            val door = gameData.doors.getDoor(doorId)
            val entity = Entity(doorId, InputEmpty(), PhysicsDoor(door), GraphicsDoor(door))
            return CutsceneActor(entity, EntityState.IDLE)
        }

        fun createFlame(): CutsceneActor {
            val entity = Entity("flame", InputEmpty(), PhysicsEmpty(), GraphicsFlame())
            return CutsceneActor(entity, EntityState.WALKING)
        }
    }

    override fun act(dt: Float) {
        entity.send(StateEvent(entityState))
        entity.send(DirectionEvent(direction))
        stateTime = getStateTime(dt)
        val region = entity.getAnimation().getKeyFrame(stateTime)
        (drawable as TextureRegionDrawable).region = region
        super.act(dt)
    }

    private fun getStateTime(dt: Float): Float {
        return when (entityState) {
            EntityState.WALKING, EntityState.RUNNING -> (stateTime + dt) % 12
            EntityState.OPENED -> stateTime + dt
            else -> Constant.NO_FRAMES
        }
    }

}
