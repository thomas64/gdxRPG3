package nl.t64.cot.constants

import com.badlogic.gdx.graphics.Color


object Constant {

    const val TITLE = "Adan Chronicles: Crystal of Time"
    const val SCREEN_WIDTH = 1920
    const val SCREEN_HEIGHT = 1080
//    const val SCREEN_WIDTH = 1280
//    const val SCREEN_HEIGHT = 720

    const val TILE_SIZE = 48f
    const val HALF_TILE_SIZE = 24f
    const val FACE_SIZE = 144f
    const val SPRITE_GROUP_WIDTH = 144
    const val SPRITE_GROUP_HEIGHT = 192

    const val MOVE_SPEED_1 = 48f    // = pixels/second
    const val MOVE_SPEED_2 = 144f   // 48 * 3
    const val MOVE_SPEED_3 = 192f   // 48 * 4
    const val MOVE_SPEED_4 = 720f   // 48 * 15

    const val SLOW_FRAMES = 0.50f
    const val NORMAL_FRAMES = 0.25f
    const val FAST_FRAMES = 0.15f
    const val NO_FRAMES = 0f

    const val FADE_DURATION = 0.5f
    const val DIALOG_FADE_OUT_DURATION = 0.4f - 0.1f

    val TRANSPARENT = Color(0f, 0f, 0f, 0.5f)
    val DARK_RED = Color(0.5f, 0f, 0f, 1f)
    val LIGHT_RED = Color(0.75f, 0.25f, 0.25f, 1f)
    val GRAY = Color(0.63f, 0.63f, 0.63f, 1f) // -0x5f5f5f01, 0xa0a0a0ff

    const val PLAYER_ID = "mozes"
    const val TRANSFORMATION_ORC = "orc_1"
    const val STARTING_MAP = "honeywood_house_mozes"

    const val DESCRIPTION_KEY_BUY = "Price"
    const val DESCRIPTION_KEY_BUY_PIECE = "Price per piece"
    const val DESCRIPTION_KEY_BUY_TOTAL = "Price for all"
    const val DESCRIPTION_KEY_SELL = "Sell value"
    const val DESCRIPTION_KEY_SELL_PIECE = "Sell value per piece"
    const val DESCRIPTION_KEY_SELL_TOTAL = "Sell value for all"

    const val PHRASE_ID_INN_NEGATIVE = "1000"
    const val PHRASE_ID_LOOT_LEFTOVER = "1199"
    const val PHRASE_ID_LOOT_TAKEN = "1200"
    const val PHRASE_ID_REWARD_LEFTOVER = "1399"
    const val PHRASE_ID_REWARD_TAKEN = "1400"
    const val PHRASE_ID_NO_CONDITIONS = "2000"
    const val PHRASE_ID_PARTY_FULL = "9000"

    const val KEYCODE_L1 = 1000
    const val KEYCODE_R1 = 1001
    const val KEYCODE_START = 1002
    const val KEYCODE_SELECT = 1003
    const val KEYCODE_TOP = 1004
    const val KEYCODE_LEFT = 1005
    const val KEYCODE_RIGHT = 1006
    const val KEYCODE_BOTTOM = 1007
    const val KEYCODE_L3 = 1008
    const val KEYCODE_R3 = 1009

}
