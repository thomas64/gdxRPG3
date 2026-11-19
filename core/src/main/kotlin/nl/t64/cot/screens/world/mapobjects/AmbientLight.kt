package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.screens.world.Daylight


enum class AmbientLight {
    DAWN {
        override val color: Color = Color(0.75f, 0.65f, 0.75f, 1f);
    },
    DAY {
        override val color: Color = Color(1f, 1f, 1f, 1f);
    },
    SUNSET {
        override val color: Color = Color(1f, 0.8f, 0.6f, 1f);
    },
    DUSK {
        override val color: Color = Color(0.45f, 0.5f, 0.7f, 1f);
    },
    RAIN {
        override val color: Color = Color(0.65f, 0.7f, 0.8f, 1f);
    },
    DAY_CYCLE {
        override val color: Color get() = Daylight.getTint();
    };

    abstract val color: Color

    companion object {
        fun of(id: String): AmbientLight {
            return valueOf(id.trim().uppercase())
        }
    }
}
