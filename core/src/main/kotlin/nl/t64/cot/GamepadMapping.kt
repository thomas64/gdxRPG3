package nl.t64.cot

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import de.golfgl.gdx.controllers.mapping.ConfiguredInput
import de.golfgl.gdx.controllers.mapping.ControllerMappings
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter
import nl.t64.cot.constants.Constant


private const val LEFT_AXIS_VERTICAL = 0
private const val LEFT_AXIS_HORIZONTAL = 1
private const val D_PAD_UP = 2
private const val D_PAD_DOWN = 3
private const val D_PAD_LEFT = 4
private const val D_PAD_RIGHT = 5
private const val RIGHT_AXIS_VERTICAL = 6
private const val RIGHT_AXIS_HORIZONTAL = 7

private const val STICK_LEFT = 8
private const val STICK_RIGHT = 9
private const val SHOULDER_LEFT = 10
private const val SHOULDER_RIGHT = 11
private const val START_BUTTON = 12
private const val SELECT_BUTTON = 13
private const val TOP_BUTTON = 14
private const val LEFT_BUTTON = 15
private const val RIGHT_BUTTON = 16
private const val BOTTOM_BUTTON = 17

class GamepadMapping {

    private val gamepadMapping: GamepadMappingPrivate = GamepadMappingPrivate()
    val controllerToInputAdapter: ControllerToInputAdapter = gamepadMapping.createControllerToInputAdapter()

    fun setInputProcessor(inputProcessor: InputProcessor?) {
        controllerToInputAdapter.inputProcessor = inputProcessor
    }

}

private class GamepadMappingPrivate : ControllerMappings() {

    init {
        setInput()
        super.commitConfig()
    }

    private fun setInput() {
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.axisDigital, LEFT_AXIS_VERTICAL))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.axisDigital, LEFT_AXIS_HORIZONTAL))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, D_PAD_UP))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, D_PAD_DOWN))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, D_PAD_LEFT))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, D_PAD_RIGHT))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.axisDigital, RIGHT_AXIS_VERTICAL))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.axisDigital, RIGHT_AXIS_HORIZONTAL))

        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, STICK_LEFT))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, STICK_RIGHT))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, SHOULDER_LEFT))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, SHOULDER_RIGHT))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, START_BUTTON))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, SELECT_BUTTON))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, TOP_BUTTON))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, LEFT_BUTTON))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, RIGHT_BUTTON))
        addConfiguredInput(ConfiguredInput(ConfiguredInput.Type.button, BOTTOM_BUTTON))
    }

    fun createControllerToInputAdapter(): ControllerToInputAdapter {
        return ControllerToInputAdapter(this).apply {
            addAxisMapping(LEFT_AXIS_VERTICAL, Input.Keys.UP, Input.Keys.DOWN)
            addAxisMapping(LEFT_AXIS_HORIZONTAL, Input.Keys.LEFT, Input.Keys.RIGHT)
            addButtonMapping(D_PAD_UP, Input.Keys.UP)
            addButtonMapping(D_PAD_DOWN, Input.Keys.DOWN)
            addButtonMapping(D_PAD_LEFT, Input.Keys.LEFT)
            addButtonMapping(D_PAD_RIGHT, Input.Keys.RIGHT)

            addButtonMapping(STICK_LEFT, Constant.KEYCODE_L3)
            addButtonMapping(STICK_RIGHT, Constant.KEYCODE_R3)
            addButtonMapping(SHOULDER_LEFT, Constant.KEYCODE_L1)
            addButtonMapping(SHOULDER_RIGHT, Constant.KEYCODE_R1)
            addButtonMapping(START_BUTTON, Constant.KEYCODE_START)
            addButtonMapping(SELECT_BUTTON, Constant.KEYCODE_SELECT)
            addButtonMapping(TOP_BUTTON, Constant.KEYCODE_TOP)
            addButtonMapping(LEFT_BUTTON, Constant.KEYCODE_LEFT)
            addButtonMapping(RIGHT_BUTTON, Constant.KEYCODE_RIGHT)
            addButtonMapping(BOTTOM_BUTTON, Constant.KEYCODE_BOTTOM)
        }
    }

    override fun getDefaultMapping(defaultMapping: MappedInputs, controller: Controller): Boolean {
        defaultMapping.putMapping(MappedInput(LEFT_AXIS_VERTICAL, ControllerAxis(controller.mapping.axisLeftY)))
        defaultMapping.putMapping(MappedInput(LEFT_AXIS_HORIZONTAL, ControllerAxis(controller.mapping.axisLeftX)))
        defaultMapping.putMapping(MappedInput(D_PAD_UP, ControllerButton(controller.mapping.buttonDpadUp)))
        defaultMapping.putMapping(MappedInput(D_PAD_DOWN, ControllerButton(controller.mapping.buttonDpadDown)))
        defaultMapping.putMapping(MappedInput(D_PAD_LEFT, ControllerButton(controller.mapping.buttonDpadLeft)))
        defaultMapping.putMapping(MappedInput(D_PAD_RIGHT, ControllerButton(controller.mapping.buttonDpadRight)))
        defaultMapping.putMapping(MappedInput(RIGHT_AXIS_VERTICAL, ControllerAxis(controller.mapping.axisRightY)))
        defaultMapping.putMapping(MappedInput(RIGHT_AXIS_HORIZONTAL, ControllerAxis(controller.mapping.axisRightX)))

        defaultMapping.putMapping(MappedInput(STICK_LEFT, ControllerButton(controller.mapping.buttonLeftStick)))
        defaultMapping.putMapping(MappedInput(STICK_RIGHT, ControllerButton(controller.mapping.buttonRightStick)))
        defaultMapping.putMapping(MappedInput(SHOULDER_LEFT, ControllerButton(controller.mapping.buttonL1)))
        defaultMapping.putMapping(MappedInput(SHOULDER_RIGHT, ControllerButton(controller.mapping.buttonR1)))
        defaultMapping.putMapping(MappedInput(START_BUTTON, ControllerButton(controller.mapping.buttonStart)))
        defaultMapping.putMapping(MappedInput(SELECT_BUTTON, ControllerButton(controller.mapping.buttonBack)))
        defaultMapping.putMapping(MappedInput(TOP_BUTTON, ControllerButton(controller.mapping.buttonY)))
        defaultMapping.putMapping(MappedInput(LEFT_BUTTON, ControllerButton(controller.mapping.buttonX)))
        defaultMapping.putMapping(MappedInput(RIGHT_BUTTON, ControllerButton(controller.mapping.buttonB)))
        defaultMapping.putMapping(MappedInput(BOTTOM_BUTTON, ControllerButton(controller.mapping.buttonA)))
        return true
    }

}
