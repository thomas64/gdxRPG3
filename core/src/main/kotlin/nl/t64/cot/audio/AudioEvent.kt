package nl.t64.cot.audio

import com.badlogic.gdx.math.MathUtils


enum class AudioEvent(val filePath: String, val volume: Float = 1f) {

    BGM_TITLE("audio/bgm/brave.mp3", 0.1f),
    BGM_FOREST("audio/bgm/journey.ogg", 0.1f),
    BGM_HONEYWOOD("audio/bgm/town.ogg", 0.1f),
    BGM_INN("audio/bgm/store.ogg", 0.1f),
    BGM_HOUSE("audio/bgm/wake_up.ogg", 0.1f),
    BGM_CELLAR("audio/bgm/quiet_town.ogg", 0.1f),
    BGM_YLARUS("audio/bgm/woods.ogg", 0.1f),
    BGM_TENSION("audio/bgm/volcano.ogg", 0.1f),
    BGM_CAVE("audio/bgm/den.ogg", 0.1f),
    BGM_GREAT_TREE("audio/bgm/wayfarer.mp3", 0.1f),
    BGM_LASTDENN("audio/bgm/search.ogg", 0.1f),
    BGM_TEMPLE("audio/bgm/find.ogg", 0.1f),
    BGM_BAR("audio/bgm/seashore.ogg", 0.1f),
    BGM_CELL("audio/bgm/spelunk.ogg", 0.1f),
    BGM_GHOST("audio/bgm/shadow.ogg", 0.1f),
    BGM_MURDER("audio/bgm/go_out.mp3", 0.1f),
    BGM_ARDOR("audio/bgm/approach.ogg", 0.1f),
    BGM_END_NEAR("audio/bgm/final_hours.ogg", 0.05f),
    BGM_VICTORY("audio/bgm/riverside_ride.ogg", 0.1f),
    BGM_MYSTERIOUS_TUNNEL("audio/bgm/cave.ogg", 0.1f),

    BGS_BIRDS("audio/bgs/birds.ogg", 0.2f),
    BGS_CREEK("audio/bgs/creek.ogg", 0.2f),
    BGS_FIREPLACE("audio/bgs/fireplace.ogg", 0.5f),
    BGS_SPRING_FLOW("audio/bgs/spring_flow.ogg", 0.2f),
    BGS_CROWS("audio/bgs/crows.ogg", 0.2f),
    BGS_RIVER("audio/bgs/mv_river.ogg", 0.2f),
    BGS_QUAKE("audio/bgs/mv_quake.ogg", 0.2f),
    BGS_END("audio/bgs/dark_scifi_drone_mixed_044.ogg", 1f),

    SE_MENU_CURSOR("audio/se/botw_menu_cursor.wav"),
    SE_MENU_CONFIRM("audio/se/botw_menu_confirm.wav"),
    SE_MENU_BACK("audio/se/botw_menu_back.wav"),
    SE_MENU_ERROR("audio/se/botw_menu_error.wav"),
    SE_MENU_TYPING("audio/se/botw_menu_typing.wav"),

    SE_CONVERSATION_START("audio/se/botw_conversation_start.ogg", 0.8f),
    SE_CONVERSATION_NEXT("audio/se/botw_conversation_next.wav", 0.4f),
    SE_CONVERSATION_END("audio/se/botw_conversation_end.wav", 0.8f),
    SE_CONVERSATION_CURSOR("audio/se/botw_conversation_cursor.wav", 0.2f),
    SE_MINIMAP("audio/se/botw_minimap.wav"),

    SE_REWARD("audio/se/virix_reward.wav", 0.4f),
    SE_QUEST_FAIL("audio/se/virix_quest_fail.wav", 0.5f),
    SE_JOIN("audio/se/virix_join.wav", 0.6f),
    SE_SAVE_GAME("audio/se/virix_save.wav", 0.3f),

    SE_CHEST("audio/se/kenney_door_open2.ogg", 0.1f),
    SE_EQUIP("audio/se/kenney_cloth3.ogg"),
    SE_TAKE("audio/se/kenney_handle_small_leather2.ogg"),
    SE_COINS_BUY("audio/se/kenney_handle_coins.ogg", 0.5f),
    SE_COINS_SELL("audio/se/kenney_handle_coins2.ogg", 0.5f),

    SE_WARP("audio/se/whooshes_impacts_impact_01.ogg", 0.4f),
    SE_BANG("audio/se/whooshes_impacts2_whooshes_032.ogg", 0.4f),
    SE_MAGIC("audio/se/just_transitions_creepy_008.ogg", 0.5f),
    SE_RESET("audio/se/just_transitions_creepy_031.ogg", 0.5f),
    SE_LEVELUP("audio/se/copyc4t_levelup.ogg", 0.3f),
    SE_DEATH_SCREAM("audio/se/death_7_ian.ogg"),

    SE_FIGHT_ON("audio/se/fight_on.ogg", 0.2f),
    SE_RESTORE("audio/se/healing.ogg", 0.2f),
    SE_UPGRADE("audio/se/upgrade.ogg", 0.5f),

    SE_MAGIC_BANG("audio/se/mv_collapse2.ogg", 0.2f),
    SE_SCROLL("audio/se/mv_book2.ogg", 0.2f),
    SE_SPARKLE("audio/se/mv_key.ogg", 0.2f),
    SE_SMALL_DOOR("audio/se/mv_open1.ogg", 0.2f),
    SE_METAL_GATE("audio/se/mv_open3.ogg", 0.2f),
    SE_LARGE_DOOR("audio/se/mv_open5.ogg", 0.2f),
    SE_WOODEN_GATE("audio/se/mv_earth4.ogg", 0.2f),
    SE_ACTIVATE("audio/se/mv_dlc_up10_a.ogg", 0.2f),

    SE_STEP_CARPET1("audio/se/footsteps/oot_step_carpet1.ogg"),
    SE_STEP_CARPET2("audio/se/footsteps/oot_step_carpet2.ogg"),
    SE_STEP_CARPET3("audio/se/footsteps/oot_step_carpet3.ogg"),
    SE_STEP_CARPET4("audio/se/footsteps/oot_step_carpet4.ogg"),
    SE_STEP_GRASS1("audio/se/footsteps/oot_step_grass1.ogg"),
    SE_STEP_GRASS2("audio/se/footsteps/oot_step_grass2.ogg"),
    SE_STEP_GRASS3("audio/se/footsteps/oot_step_grass3.ogg"),
    SE_STEP_GRASS4("audio/se/footsteps/oot_step_grass4.ogg"),
    SE_STEP_SAND1("audio/se/footsteps/oot_step_sand1.ogg"),
    SE_STEP_SAND2("audio/se/footsteps/oot_step_sand2.ogg"),
    SE_STEP_SAND3("audio/se/footsteps/oot_step_sand3.ogg"),
    SE_STEP_SAND4("audio/se/footsteps/oot_step_sand4.ogg"),
    SE_STEP_STONE1("audio/se/footsteps/oot_step_stone1.ogg"),
    SE_STEP_STONE2("audio/se/footsteps/oot_step_stone2.ogg"),
    SE_STEP_STONE3("audio/se/footsteps/oot_step_stone3.ogg"),
    SE_STEP_STONE4("audio/se/footsteps/oot_step_stone4.ogg"),
    SE_STEP_WOOD1("audio/se/footsteps/oot_step_wood1.ogg"),
    SE_STEP_WOOD2("audio/se/footsteps/oot_step_wood2.ogg"),
    SE_STEP_WOOD3("audio/se/footsteps/oot_step_wood3.ogg"),
    SE_STEP_WOOD4("audio/se/footsteps/oot_step_wood4.ogg"),
    SE_STEP_WATER1("audio/se/footsteps/oot_step_water1.wav"),
    SE_STEP_WATER2("audio/se/footsteps/oot_step_water2.wav"),
    SE_STEP_WATER3("audio/se/footsteps/oot_step_water3.wav"),
    SE_STEP_WATER4("audio/se/footsteps/oot_step_water4.wav"),

    NONE("");

}

fun String.toAudioEvent(): AudioEvent {
    return AudioEvent.valueOf("SE_STEP_" + this.uppercase() + MathUtils.random(1, 4))
}
