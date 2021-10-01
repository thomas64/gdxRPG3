package nl.t64.cot;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerMapping;
import de.golfgl.gdx.controllers.mapping.ConfiguredInput;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter;
import nl.t64.cot.constants.Constant;


public class GamepadMapping extends ControllerMappings {

    private static final int LEFT_AXIS_VERTICAL = 0;
    private static final int LEFT_AXIS_HORIZONTAL = 1;
    private static final int D_PAD_UP = 2;
    private static final int D_PAD_DOWN = 3;
    private static final int D_PAD_LEFT = 4;
    private static final int D_PAD_RIGHT = 5;
    private static final int RIGHT_AXIS_VERTICAL = 6;
    private static final int RIGHT_AXIS_HORIZONTAL = 7;

    private static final int STICK_LEFT = 8;
    private static final int STICK_RIGHT = 9;
    private static final int SHOULDER_LEFT = 10;
    private static final int SHOULDER_RIGHT = 11;
    private static final int START_BUTTON = 12;
    private static final int SELECT_BUTTON = 13;
    private static final int TOP_BUTTON = 14;
    private static final int LEFT_BUTTON = 15;
    private static final int RIGHT_BUTTON = 16;
    private static final int BOTTOM_BUTTON = 17;

    private static final int TRIGGER_LEFT = 18;
    private static final int TRIGGER_RIGHT = 19;

    final ControllerToInputAdapter controllerToInputAdapter;

    GamepadMapping() {
        this.setInput();
        super.commitConfig();
        this.controllerToInputAdapter = this.createControllerToInputAdapter();
    }

    public void setInputProcessor(InputProcessor input) {
        controllerToInputAdapter.setInputProcessor(input);
    }

    @Override
    public boolean getDefaultMapping(MappedInputs defaultMapping, Controller controller) {
        ControllerMapping mapping = controller.getMapping();
        defaultMapping.putMapping(new MappedInput(LEFT_AXIS_VERTICAL, new ControllerAxis(mapping.axisLeftY)));
        defaultMapping.putMapping(new MappedInput(LEFT_AXIS_HORIZONTAL, new ControllerAxis(mapping.axisLeftX)));
        defaultMapping.putMapping(new MappedInput(D_PAD_UP, new ControllerButton(mapping.buttonDpadUp)));
        defaultMapping.putMapping(new MappedInput(D_PAD_DOWN, new ControllerButton(mapping.buttonDpadDown)));
        defaultMapping.putMapping(new MappedInput(D_PAD_LEFT, new ControllerButton(mapping.buttonDpadLeft)));
        defaultMapping.putMapping(new MappedInput(D_PAD_RIGHT, new ControllerButton(mapping.buttonDpadRight)));
        defaultMapping.putMapping(new MappedInput(RIGHT_AXIS_VERTICAL, new ControllerAxis(mapping.axisRightY)));
        defaultMapping.putMapping(new MappedInput(RIGHT_AXIS_HORIZONTAL, new ControllerAxis(mapping.axisRightX)));

        defaultMapping.putMapping(new MappedInput(STICK_LEFT, new ControllerButton(mapping.buttonLeftStick)));
        defaultMapping.putMapping(new MappedInput(STICK_RIGHT, new ControllerButton(mapping.buttonRightStick)));
        defaultMapping.putMapping(new MappedInput(SHOULDER_LEFT, new ControllerButton(mapping.buttonL1)));
        defaultMapping.putMapping(new MappedInput(SHOULDER_RIGHT, new ControllerButton(mapping.buttonR1)));

        defaultMapping.putMapping(new MappedInput(TRIGGER_LEFT, new ControllerButton(mapping.buttonL2)));
        defaultMapping.putMapping(new MappedInput(TRIGGER_RIGHT, new ControllerButton(mapping.buttonR2)));

        defaultMapping.putMapping(new MappedInput(START_BUTTON, new ControllerButton(mapping.buttonStart)));
        defaultMapping.putMapping(new MappedInput(SELECT_BUTTON, new ControllerButton(mapping.buttonBack)));
        defaultMapping.putMapping(new MappedInput(TOP_BUTTON, new ControllerButton(mapping.buttonY)));
        defaultMapping.putMapping(new MappedInput(LEFT_BUTTON, new ControllerButton(mapping.buttonX)));
        defaultMapping.putMapping(new MappedInput(RIGHT_BUTTON, new ControllerButton(mapping.buttonB)));
        defaultMapping.putMapping(new MappedInput(BOTTOM_BUTTON, new ControllerButton(mapping.buttonA)));
        return true;
    }

    private void setInput() {
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, LEFT_AXIS_VERTICAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, LEFT_AXIS_HORIZONTAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, D_PAD_UP));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, D_PAD_DOWN));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, D_PAD_LEFT));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, D_PAD_RIGHT));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, RIGHT_AXIS_VERTICAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, RIGHT_AXIS_HORIZONTAL));

        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, STICK_LEFT));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, STICK_RIGHT));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, SHOULDER_LEFT));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, SHOULDER_RIGHT));

        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, TRIGGER_LEFT));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, TRIGGER_RIGHT));

        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, START_BUTTON));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, SELECT_BUTTON));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, TOP_BUTTON));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, LEFT_BUTTON));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, RIGHT_BUTTON));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BOTTOM_BUTTON));
    }

    private ControllerToInputAdapter createControllerToInputAdapter() {
        ControllerToInputAdapter adapter = new ControllerToInputAdapter(this);
        adapter.addAxisMapping(LEFT_AXIS_VERTICAL, Input.Keys.UP, Input.Keys.DOWN);
        adapter.addAxisMapping(LEFT_AXIS_HORIZONTAL, Input.Keys.LEFT, Input.Keys.RIGHT);
        adapter.addButtonMapping(D_PAD_UP, Input.Keys.UP);
        adapter.addButtonMapping(D_PAD_DOWN, Input.Keys.DOWN);
        adapter.addButtonMapping(D_PAD_LEFT, Input.Keys.LEFT);
        adapter.addButtonMapping(D_PAD_RIGHT, Input.Keys.RIGHT);
        adapter.addAxisMapping(RIGHT_AXIS_HORIZONTAL, Constant.KEYCODE_R3_L, Constant.KEYCODE_R3_R);

        adapter.addButtonMapping(STICK_LEFT, Constant.KEYCODE_L3);
        adapter.addButtonMapping(STICK_RIGHT, Constant.KEYCODE_R3);
        adapter.addButtonMapping(SHOULDER_LEFT, Constant.KEYCODE_L1);
        adapter.addButtonMapping(SHOULDER_RIGHT, Constant.KEYCODE_R1);

        adapter.addButtonMapping(TRIGGER_LEFT, Constant.KEYCODE_L1);
        adapter.addButtonMapping(TRIGGER_RIGHT, Constant.KEYCODE_R1);

        adapter.addButtonMapping(START_BUTTON, Constant.KEYCODE_START);
        adapter.addButtonMapping(SELECT_BUTTON, Constant.KEYCODE_SELECT);
        adapter.addButtonMapping(TOP_BUTTON, Constant.KEYCODE_TOP);
        adapter.addButtonMapping(LEFT_BUTTON, Constant.KEYCODE_LEFT);
        adapter.addButtonMapping(RIGHT_BUTTON, Constant.KEYCODE_RIGHT);
        adapter.addButtonMapping(BOTTOM_BUTTON, Constant.KEYCODE_BOTTOM);
        return adapter;
    }

}
