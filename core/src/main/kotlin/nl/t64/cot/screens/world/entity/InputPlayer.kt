package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


private const val TURN_DELAY_TIME = 8f / 60f // of a second
private const val TURN_DELAY_GRACE_PERIOD = 0.3f
private const val TURN_GRACE_MAX_VALUE = 5f

class InputPlayer(multiplexer: InputMultiplexer) : InputComponent(), InputProcessor {

    private lateinit var player: Entity

    private var pressUp = false
    private var pressDown = false
    private var pressLeft = false
    private var pressRight = false

    private var pressAlign = false

    private var turnDelay = 0f
    private var turnGrace = 0f

    private var pressCtrl = false
    private var pressShift = false
    private var pressAlt = false
    private var pressAction = false

    init {
        multiplexer.addProcessor(this)
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            direction = event.direction!!
        }
    }

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.CONTROL_LEFT
            || keycode == Input.Keys.CONTROL_RIGHT
            || keycode == Constant.KEYCODE_L1
        ) {
            pressCtrl = !pressCtrl
            pressShift = false
        }
        if (keycode == Input.Keys.SHIFT_LEFT
            || keycode == Input.Keys.SHIFT_RIGHT
            || keycode == Constant.KEYCODE_R1
        ) {
            pressShift = !pressShift
            pressCtrl = false
        }
        return keyPressed(keycode, true)
    }

    override fun keyUp(keycode: Int): Boolean {
        return keyPressed(keycode, false)
    }

    private fun keyPressed(keycode: Int, isPressed: Boolean): Boolean {
        if (keycode == Input.Keys.ALT_LEFT
            || keycode == Input.Keys.ALT_RIGHT
        ) {
            pressAlt = isPressed
        }

        if (keycode == Input.Keys.A
            || keycode == Constant.KEYCODE_BOTTOM
        ) {
            pressAction = isPressed
        }

        if (keycode == Input.Keys.UP) {
            pressUp = isPressed
        }
        if (keycode == Input.Keys.DOWN) {
            pressDown = isPressed
        }
        if (keycode == Input.Keys.LEFT) {
            pressLeft = isPressed
        }
        if (keycode == Input.Keys.RIGHT) {
            pressRight = isPressed
        }

        if (keycode == Input.Keys.SPACE) {
            pressAlign = isPressed
        }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    override fun update(entity: Entity, dt: Float) {
        this.player = entity
        processMoveInput(dt)
        processOtherInput()
    }

    override fun reset() {
        pressUp = false
        pressDown = false
        pressLeft = false
        pressRight = false

        pressAlign = false

        turnDelay = 0f
        turnGrace = 0f

        pressAction = false
    }

    override fun resetStance() {
        pressCtrl = false
        pressShift = false
    }

    private fun processMoveInput(dt: Float) {
        processPlayerSpeedInput()
        ifNoMoveKeys_SetGracePeriod(dt)
        ifNoMoveKeys_SetPlayerIdle()
        ifStandingStill_SetTurnDelay()
        setPlayerDirection()
        ifMoveKeys_SetPlayerWalking(dt)
    }

    private fun processOtherInput() {
        if (pressAlign && preferenceManager.isInDebugMode) {
            player.send(StateEvent(EntityState.ALIGNING))
        }
        if (pressAction) {
            player.send(ActionEvent())
            pressAction = false
        }
    }

    private fun processPlayerSpeedInput() {
        var moveSpeed = Constant.MOVE_SPEED_2
        when {
            pressAlt && preferenceManager.isInDebugMode -> moveSpeed = Constant.MOVE_SPEED_4
            pressShift -> moveSpeed = Constant.MOVE_SPEED_3
            pressCtrl -> moveSpeed = Constant.MOVE_SPEED_1
        }
        player.send(SpeedEvent(moveSpeed))
    }

    private fun ifNoMoveKeys_SetGracePeriod(dt: Float) {
        if (!areMoveKeysPressed()
            && turnGrace < TURN_GRACE_MAX_VALUE
        ) {
            turnGrace += dt
        }
    }

    private fun ifNoMoveKeys_SetPlayerIdle() {
        if (!areMoveKeysPressed()) {
            turnDelay = 0f
            player.send(StateEvent(EntityState.IDLE))
        }
    }

    private fun ifStandingStill_SetTurnDelay() {
        if (turnDelay <= 0f
            && turnGrace > TURN_DELAY_GRACE_PERIOD
            && (isPressUpButDirectionIsNotYetNorth()
                || isPressDownButDirectionIsNotYetSouth()
                || isPressLeftButDirectionIsNotYetWest()
                || isPressRightButDirectionIsNotYetEast())
        ) {
            turnDelay = TURN_DELAY_TIME
        }
    }

    private fun setPlayerDirection() {
        when {
            pressLeft && pressUp -> direction = Direction.NORTH_WEST
            pressRight && pressUp -> direction = Direction.NORTH_EAST
            pressLeft && pressDown -> direction = Direction.SOUTH_WEST
            pressRight && pressDown -> direction = Direction.SOUTH_EAST

            pressUp -> direction = Direction.NORTH
            pressDown -> direction = Direction.SOUTH
            pressLeft -> direction = Direction.WEST
            pressRight -> direction = Direction.EAST
        }
    }

    private fun ifMoveKeys_SetPlayerWalking(dt: Float) {
        if (areMoveKeysPressed()) {
            player.send(DirectionEvent(direction))

            if (turnDelay > 0f) {
                turnDelay -= dt
            } else {
                turnGrace = 0f
                player.send(StateEvent(EntityState.WALKING))
            }
        }
    }

    private fun areMoveKeysPressed(): Boolean {
        val pressedCount = listOf(pressUp, pressDown, pressLeft, pressRight).count { it }
        if (pressedCount == 0 || pressedCount == 3 || pressedCount == 4) {
            return false
        }
        if ((pressLeft && pressRight) || (pressUp && pressDown)) {
            return false
        }
        return true
    }

    private fun isPressUpButDirectionIsNotYetNorth(): Boolean {
        return pressUp && !direction.isNorth()
    }

    private fun isPressDownButDirectionIsNotYetSouth(): Boolean {
        return pressDown && !direction.isSouth()
    }

    private fun isPressLeftButDirectionIsNotYetWest(): Boolean {
        return pressLeft && !direction.isWest()
    }

    private fun isPressRightButDirectionIsNotYetEast(): Boolean {
        return pressRight && !direction.isEast()
    }

}
